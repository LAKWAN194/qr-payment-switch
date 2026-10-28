# Synthetic transaction generator (week 5)

Produces labelled payment traffic for training and testing the fraud model, and for
load tests. Nothing here yet.

It should generate normal behaviour per payer (typical amounts, times of day,
favourite merchants) and inject known fraud patterns at a controlled rate:

- **Velocity:** many payments from one payer or device in a short time
- **Amount spikes:** a payment far above the payer's usual range
- **New device:** a payer suddenly paying from a device never seen before
- add your own, and say why each is realistic

Write the patterns and their rates down before generating data, so the model's
results can be judged against them. Keep a fixed random seed, so a dataset can be
regenerated exactly.
