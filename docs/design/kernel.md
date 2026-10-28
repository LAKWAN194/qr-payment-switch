# The kernel: EMV QR codec and CRC

The kernel is the one piece of this project you write entirely by hand, without
generated code, so you can rebuild it on a whiteboard from memory. It lives in
`switch-service/src/main/java/io/github/lakwan194/qrswitch/qr/`.

## Rules

1. **No generated code in this package.** Read the spec, sketch it on paper,
   then type it yourself.
2. **No framework code.** `KernelIsolationTest` fails the build if anything in
   `qr` depends on Spring, JPA, servlets or the rest of the service. Plain Java
   only.
3. **Tests first.** Enable `Crc16Test`, watch it fail, then make it pass. Write
   your codec tests before the codec.
4. **Explain every line.** If you cannot say why a line is there, it should not
   be.

## What exists already

- `Crc16.ccittFalse(byte[])`: a stub that throws. Its Javadoc states the CRC
  parameters from the spec.
- `Crc16Test`: three known-answer tests, disabled until you implement it.
- `src/test/resources/emv-qr/vectors.tsv`: six sample payloads with the outcome
  each should produce. Their checksums come from Python's standard library, so
  they check your code independently.

Nothing else exists. The codec's API is your design.

## Your design (fill in)

### Responsibilities

<!-- What does the kernel do, and what does it deliberately not do?
     For example: it knows TLV and CRC; it does not know that LKR is the only currency. -->

### Public API

<!-- The types and methods other packages call. Sketch them here before writing them.
     Questions: How does a caller build a payload? What does parsing return? How are
     errors reported: exceptions, a result type, or a list of problems? Are the
     types immutable? -->

### Strictness rules

<!-- Answer the questions at the end of docs/domain/emv-qr-mpm.md, one line each,
     with the spec clause where there is one. -->

| Question | Your rule | Spec clause |
| --- | --- | --- |
| Unknown ID | | |
| Duplicate ID | | |
| Ordering | | |
| Length past the end | | |
| Static code with amount / dynamic without | | |
| Format rules vs business rules | | |
| CRC before or after parsing | | |

### Errors

<!-- List each way a payload can be invalid and what the caller receives for it.
     Map them to the outcomes in vectors.tsv: BAD_CRC, BAD_STRUCTURE,
     MISSING_MANDATORY, BAD_ORDER. -->

## Tests to write

Known-answer tests cover the spec. **Property-based tests** (jqwik is already a
test dependency) cover what you did not think of. Properties worth trying:

- Building a payload from fields, then parsing it, gives back the same fields
  (round trip).
- Changing any single character of a valid payload makes it invalid.
- Every length field equals the number of characters that follow it.
- The CRC of a built payload always verifies.
- Parsing never throws anything except your declared error type, whatever the
  input.

## Done when

- [ ] `Crc16Test` is enabled and passes
- [ ] Every line of `vectors.tsv` produces its expected outcome
- [ ] At least four property-based tests pass
- [ ] You can write the CRC loop and the TLV parsing loop on paper, from memory,
      and explain each line
