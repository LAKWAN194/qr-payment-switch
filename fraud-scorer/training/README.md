# Training (week 5)

This folder will hold the code that turns synthetic transactions into a fraud model.

1. Generate labelled transactions with `tools/txn-generator` (velocity bursts, amount
   spikes, new devices, and normal traffic).
2. Split them by time, not at random, so the model is tested on transactions that
   happen after the ones it learned from.
3. Train XGBoost and compare it with a simple baseline (for example a rules-only
   scorer or logistic regression).
4. Choose the threshold from the precision-recall curve, and write down why that
   point: what a false positive costs a merchant, and what a missed fraud costs.
5. Save the model to `training/artifacts/` together with a model card: the data,
   features, metrics at the chosen threshold, and known weaknesses.

Install the training dependencies with `pip install -r requirements-train.txt`.
