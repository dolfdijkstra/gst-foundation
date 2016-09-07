<!--
 Copyright 2012 Oracle Corporation. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

#GST Site Foundation Overview

The GSF aims at:

	1.	Standardizing processes for business users.

	2.	Simplifying coding greatly, to the point where much can be provided in common libraries.

	3.	Clearly defining extension points.
	
	4. Not imposing any design / data model requirements whilst encouraging the use of design patterns, optimal coding and best practices.   

At a high level, the GSF offers a non-rendering foundation which aims at providing the following primary benefits:

	*	Is suitable for use "out of the box" for a large number of sites

	*	Standardizes usage of the Page asset for modeling navigation (e.g. Site Maps, Menus, etc...)
	
	*	Extends Webcenter Sites's controller infrastructure, leveraging clean, reusable code and productivity 

	*	Can be cleanly extended to address custom client requirements

This is the first GSF release which embraces WCS 12c's features and rendering practices.

The main goals of this specific release were:

	1.	Providing the means for clients out there already using GSF to reuse as much of their existing code as possible when upgrading from WCS 11.x to WCS 12c,

	2.	Leveraging the use of WCS 12c's new features / API,

	3.	Simplifying GSF's installation / deployment to the utmost and making it as agile and CI-friendly as possible, and

	4.	Encouraging GSF users and potential adopters to benefit from GSF's optimized code whilst leveraging better encapsulation, cleaner design and more intuitive, simpler usage and deployment.

Many of the GSF's existing features have become partially or totally unnecessary due to WCS 12c (and 11.x) offering a native equivalent.

Such features are getting deprecated in this release and, in some cases, physically removed from the GSF project's codebase.

Those classes / components which are still valid and/or useful and/or expected to become building blocks for NEW features to be
implemented in future releases of the GSF have been left untouched -- or adjusted specifically for WCS 12c.

Deprecated features will be physically removed from the GSF project's codebase in future major releases. We still aim at keeping the GSF's code - and footprint - as clean and minimal as possible!

We understand the complexities inherent to your replacing GSF features with WCS 12c's native ones -- and that it may imply
your rewriting existing code and releasing it whilst at it.

That is why, for this release, we are exceptionally supporting your using most deprecated (GSF) features in WCS 12c; with
the some limitations / tradeoff, obviously.

We expect that will allow your transitioning onto GSF-12's (and WCS 12c's) paradigms / API / features as
soon and smoothly as possible.

GSF-12 also acknowledges the small but mission-critical differences that sometimes exist amongst WCS implementations
out there.

In this regard, GSF-12's roadmap contemplates reengineering some of the core features in previous versions - such as the
Navigation Service - so tailoring them to address those small differences is as painless as possible.

Developers and Architects are encouraged to build upon - and extend - this framework with the long-term goal of folding
in such enhancements into future versions of the GST Site Foundation.

We expect cool new features to be built upon the GSF-12's new codebase in the upcoming minor releases!

September, 2016.

##An Important Note on Backwards-Compatibility

All of the GSF's pre-existing classes have been **deprecated** in this release.

The new namespace is: **tools.gsf**.

Starting this initial release of GSF-12, new "CORE" classes will belong to a subpackage of "tools.gsf" (or "tools.gsf" itself) 

All deprecated classes have been packaged inside a separate artifact / JAR file called "LEGACY". These enter the GSF	deprecation cycle, which dictates that deprecated components will be physically removed from the GSF project's codebase in the next major release after deprecation occurs.

Deprecated classes will not be enhanced, ever. Only the CORE classes will.

As CORE features / classes evolve they will gradually diverge from their LEGACY counterpart, whenever applicable.  

**You are strongly advised to start using the new namespace (e.g. packages / classnames) right away.** 

##Documentation

* GSF-12 Installation Guide (InstallGuide.apt) - Guide for manually installing and configuring GSF-12. The website's installation guide is generated off this APT file.

* JavaDocs are included in this kit. Alternatively, you can get them from the GSF's website.

##Contents

In this release, classes are shipped in 2 JAR files:
    
	* gsf-core-<version>.jar: here you'll find the classes that make up GSF's new CORE.    
    
	* gsf-legacy-<version>.jar: here you'll find deprecated classes scheduled for physical removal in a future release.
	
This kit builds both JAR files, including the due JavaDocs. 

##Requirements

Java 1.8 (or newer)
Servlet 3.0
WCS 12c
SLF4J 1.7.21 (or newer) -- NOTE: WCS 12c already ships with SLF4J

##Support

The Global Site Foundation is supported by the community. Questions and remarks can be send to:

	ips-link@yahoogroups.com. 

Oracle Support does NOT provide support on GSF.

Issues / Bugs can be entered directly into our GitHub project's page:

	https://github.com/dolfdijkstra/gst-foundation/issues

##License

This project is licensed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.

