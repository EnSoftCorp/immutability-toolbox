---
layout: page
title: Install
permalink: /install/
---

Installing the Immutability Toolbox Eclipse plugin is easy.

Note: A composite Eclipse update site is coming soon!

### Installing Dependencies
1. First make sure you have [Atlas](http://www.ensoftcorp.com/atlas/download/) Standard or Pro installed. When installing Atlas make sure to also include Atlas Experimental features (to include Atlas for Jimple support).
2. Install the [Points-to Toolbox](https://ensoftcorp.github.io/points-to-toolbox/install/)

## Installing from Source
If you want to install from source for bleeding edge changes, first grab a copy of the [source](https://github.com/EnSoftCorp/immutability-toolbox) repository. In the Eclipse workspace, import the `com.ensoftcorp.open.immutability` Eclipse project located in the source repository.  Right click on the project and select `Export`.  Select `Plug-in Development` &gt; `Deployable plug-ins and fragments`.  Select the `Install into host. Repository:` radio box and click `Finish`.  Press `OK` for the notice about unsigned software.