# EMV QR, merchant-presented mode: spec notes

LANKAQR is based on the *EMV QR Code Specification for Payment Systems,
Merchant-Presented Mode* published by EMVCo. "Merchant-presented" means the
merchant shows the code and the customer's app scans it (the opposite mode,
consumer-presented, is not used here).

These notes summarise the parts this project needs. **Read the specification
itself before you write the kernel.** It is free from EMVCo's website (see
[references.md](../references.md)), and quoting the exact clause you implemented
is a strong answer in an interview. Where these notes say "check the spec", the
answer matters and was not verified here.

## The payload is a string of TLV data objects

A payload is plain text made of **data objects**, one after another. Each object
is:

| Part | Size | Meaning |
| --- | --- | --- |
| ID | 2 digits | Which field this is, from `00` to `99` |
| Length | 2 digits | How many characters the value has, from `01` to `99` |
| Value | *length* characters | The content |

So `5303144` is ID `53` (transaction currency), length `03`, value `144`
(the ISO 4217 number for Sri Lankan rupees).

Some objects are **templates**: their value is itself a sequence of TLV objects,
with their own IDs counted from `00`. A whole payload is at most 512 characters
(check the spec for the exact limit and how it is counted).

## The data objects you will meet

| ID | Name | Presence | Notes |
| --- | --- | --- | --- |
| `00` | Payload Format Indicator | Mandatory | Value `01`. **Must be the first object.** |
| `01` | Point of Initiation Method | Optional | `11` = static (the same code for every payment), `12` = dynamic (one code per payment, usually with an amount) |
| `02`–`25` | Merchant account information (reserved) | | Reserved for card networks and EMVCo, such as Visa, Mastercard and UnionPay. At least one merchant account object is mandatory overall. |
| `26`–`51` | Merchant account information (templates) | At least one | Templates for other payment systems. Sub-object `00` is a **globally unique identifier** for the system (for example a reverse domain name); the other sub-objects are defined by that system, such as a merchant ID. |
| `52` | Merchant Category Code | Mandatory | 4 digits, ISO 18245 (for example `5812` = eating places and restaurants) |
| `53` | Transaction Currency | Mandatory | 3 digits, ISO 4217 numeric. LKR is `144`. |
| `54` | Transaction Amount | Conditional | Present on dynamic codes that fix the amount. Check the spec for the allowed format (digits and one decimal point, maximum length). |
| `55` | Tip or Convenience Indicator | Optional | `01` = ask the customer for a tip, `02` = fixed fee in `56`, `03` = percentage fee in `57` |
| `56` | Value of Convenience Fee Fixed | Conditional | Required when `55` is `02` |
| `57` | Value of Convenience Fee Percentage | Conditional | Required when `55` is `03` |
| `58` | Country Code | Mandatory | ISO 3166-1 alpha-2, so `LK` |
| `59` | Merchant Name | Mandatory | Up to 25 characters |
| `60` | Merchant City | Mandatory | Up to 15 characters |
| `61` | Postal Code | Optional | |
| `62` | Additional Data Field Template | Optional | A template. Sub-objects include `01` bill number, `02` mobile number, `03` store label, `04` loyalty number, `05` reference label, `06` customer label, `07` terminal label, `08` purpose of transaction, `09` additional consumer data request. |
| `63` | CRC | Mandatory | **Must be the last object.** Always length `04`. |
| `64` | Merchant Information, Language Template | Optional | The merchant name and city in another language. This is where Sinhala or Tamil would go. Check the spec for how lengths are counted for non-ASCII characters. |
| `65`–`79` | Reserved for future use by EMVCo | | |
| `80`–`99` | Unreserved templates | | Free for payment systems to define |

LANKAQR adds its own rules on top of this (which merchant account template it
uses, which IDs it requires). Those rules are in LankaPay's specification, which
was not available for this project. Pick your own globally unique identifier for
the merchant account template and say so in the README. The samples here use
`io.github.lakwan194.qrswitch`.

## The CRC

Object `63` holds a 16-bit checksum written as **4 hexadecimal characters**.

| Parameter | Value |
| --- | --- |
| Algorithm | CRC-16/CCITT-FALSE (the CRC catalogue calls it CRC-16/IBM-3740) |
| Polynomial | `0x1021` |
| Initial value | `0xFFFF` |
| Reflect input / output | No / No |
| Final XOR | `0x0000` |
| Check value (CRC of the ASCII string `123456789`) | `0x29B1` |
| Computed over | **Every character before the checksum, including `6304`**, which is the CRC object's own ID and length |

The spec shows the checksum in upper-case hex. Whether a parser must reject
lower-case is a question to settle from the spec text.

## A worked example

This static payload is in `switch-service/src/test/resources/emv-qr/vectors.tsv`
as `static-valid`:

```
00020101021126430028io.github.lakwan194.qrswitch0107M0001235204581253031445802LK5916Kandy Brew House6005Kandy6304BF84
```

Read left to right:

| Text | ID | Length | Value | Meaning |
| --- | --- | --- | --- | --- |
| `000201` | 00 | 02 | `01` | Payload format version 01 |
| `010211` | 01 | 02 | `11` | Static code |
| `2643…M000123` | 26 | 43 | (template) | Merchant account information |
| ↳ `0028io.github.lakwan194.qrswitch` | 00 | 28 | `io.github.lakwan194.qrswitch` | Globally unique identifier |
| ↳ `0107M000123` | 01 | 07 | `M000123` | Merchant ID inside that system |
| `52045812` | 52 | 04 | `5812` | Restaurant |
| `5303144` | 53 | 03 | `144` | LKR |
| `5802LK` | 58 | 02 | `LK` | Sri Lanka |
| `5916Kandy Brew House` | 59 | 16 | `Kandy Brew House` | Merchant name |
| `6005Kandy` | 60 | 05 | `Kandy` | Merchant city |
| `6304BF84` | 63 | 04 | `BF84` | CRC over everything before `BF84` |

The `dynamic-valid` sample adds `010212` (dynamic), an amount `54071250.00`, and
an additional data template `6224…` with a bill number and a terminal label.

## Questions your parser has to answer

Each one is a decision about strictness. Write your answers in the kernel design
section of [design/kernel.md](../design/kernel.md).

1. What happens with an ID your code does not know? Reject it, or keep it as an
   unknown field?
2. What if the same ID appears twice?
3. Must IDs appear in ascending order, or only `00` first and `63` last?
4. What if a length runs past the end of the payload?
5. Is a static code with an amount valid? Is a dynamic code without one?
6. Which mandatory objects are checked in the parser, and which at a higher
   level (for example "the currency must be LKR" is a business rule, not a
   format rule)?
7. Do you validate the CRC before parsing, or after?
