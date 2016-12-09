---
layout: default
---

## Overview
The Immutability Toolbox seeks to answer the question: *given code C and reference R, is the object O referenced by R mutated in the code C?*. The toolbox contains robust implementations of two prominent approaches to immutability analysis. The first approach leverages a precise points-to analysis computed by the [Points-to Toolbox](https://ensoftcorp.github.io/points-to-toolbox/) to resolve aliasing relationships and compute object immutability. The second approach is a scalable type inference based approach described by Wei Huang et al. in their [OOPSLA 2012 paper](https://huangw5.github.io/docs/oopsla12.pdf). The results of both analyses are computed natively in the [Atlas program analysis framework](http://www.ensoftcorp.com/atlas/) and used to annotated a queryable program graph produced by Atlas making the results accessible to other analysis tasks critical to software validation and verification.

## Features
- **Extensible Analysis:** Both immutability analyses classify and tag references as *READONLY*, *POLYREAD*, or *MUTABLE* to indicate the immutability of the referenced object. *POLYREAD* is used to represent context-sensitive results where a mutation may occur in one context but not another. These tags are queryable by any client analysis requiring immutability analysis results.
- **Method Purity:** From the results of the immutability analysis, the toolbox also computes method purity (a method that does not mutate objects that exist before the method invocation). Pure methods are tagged as *PURE*, whereas methods with side effects (mutations to objects existing before the method was invoked) are not.
- **Partial Program Analysis:** Both implementations support partial program analysis (e.g. analysis of libraries).
- **Well Tested:** Both approaches have been rigorously [tested and evaluated](https://kcsl.github.io/immutability-benchmark/).

## Analysis Tradeoffs
Since both implementations produce results in the same format, you can seamlessly swap out the analysis approach and choose the best analysis for your environment and task. For any industrial grade program analysis, it is important to know the *accuracy boundaries* (classes of inputs for which the tool cannot be guaranteed to report accurate results) and scalability of each approach. Since there is no single best analysis, here are the highlights.

### Points-To Based Analysis
- Most precise implementation in the toolbox
- Does not scale well beyond ~40k lines of code, but is actually faster than the inference based approach in small applications.

### Type Inference Based Analysis
- Highly scalable. We use a fast hand-optimized lookup table based constraint solver and have successfully run this algorithm over the entire JDK (several million lines of code).
- Conservative results dealing with dynamic dispatches and aliasing. For additional details see our [benchmark results](https://kcsl.github.io/immutability-benchmark/results).
- Supports incremental program analysis. Could be used to summarize mutation behaviors in libraries and enhance an application-only analysis.

## Getting Started
Ready to get started?

1. First [install](/immutability-toolbox/install) the Immutability Toolbox plugin
2. Then check out the provided [tutorials](/immutability-toolbox/tutorials) to jump start your analysis

## Source Code
Need additional resources? Checkout the [Javadocs](/immutability-toolbox/javadoc/index.html) or grab a copy of the [source](https://github.com/EnSoftCorp/immutability-toolbox).