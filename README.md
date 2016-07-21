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

	1.	Defining architectural and design standards for the foundation of a site

	2.	Standardizing processes for business users

	3.	Simplifying coding greatly, to the point where much can be provided in common libraries

	4.	Clearly defining extension points   

At a high level, the GSF offers a non-rendering foundation which aims at providing the following primary benefits:

	*	Is suitable for use "out of the box" for a large number of sites

	*	Standardizes usage of the Page asset for modeling navigation (e.g. Site Maps, Menus, etc...)

	*	Extends Webcenter Sites's controller infrastructure, leveraging clean, reusable code and productivity 

	*	Provides for managed alias assets pointing at other assets or external URLs

	*	Can be cleanly extended to address custom client requirements

This release of the GSF, GSF-12, is considered to be a "transitional" one.

The main goal is providing the means for clients out there already using GSF to reuse as much of their existing
code as possible when upgrading from WCS 11.x to WCS 12.x.

GSF features that became partially or totally unnecessary due to WCS 12.x offering them natively have been either
deprecated or wiped off the GSF-12's codebase.

Only those classes / components which are still valid and/or useful and/or expected to be building blocks for NEW
features to be implemented in future releases of the GSF-12 have been either left untouched or adjusted specifically
for WCS 12.x.

This release acknowledges the small but mission-critical differences that sometimes exist amongst WCS implementations
out there. In this regard, GSF-12's roadmap contemplates reengineering some of the core features in previous versions
- such as the Navigation Service - so they can be tailored to address those small differences, painlessly.

Developers and Architects are encouraged to build upon and extend this framework with the long-term goal of folding
in such enhancements into future versions of the GST Site Foundation.

July, 2016

##An Important Note on Backwards-Compatibility

One of the major changes introduced into the GSF codebase in version 12 (and up) is a brand new namespace for all classes and loggers.

The new namespace is: tools.gsf.

Starting the initial release of GSF-12, pre-existing classes will be progressively moved onto the LEGACY artifact.

As per the LEGACY artifact's semantic, this implies "old" classes will enter the deprecation cycle, which ends up in permanent (physical) removal from the GSF's codebase / project in periods of 12 to 24 months.

Therefore, you are strongly advised to start using the new namespace (e.g. packages / classnames). 

##Documentation

* {{{./InstallGuide.html}GSF-12 Installation ReadMe}} - Guide for manually installing and configuring GSF-12

* JavaDocs are included in this kit. Alternatively, you can get them from the GSF's website.

##Download

For this transitional release, classes are shipped in 2 JAR files:

	* gsf-core-<version>.jar: here you'll find the classes that make up the new (and temporarily slim) CORE of the GSF.    

	* gsf-legacy-<version>.jar: here you'll find the classes that have been deprecated and are planned for complete removal in future releases.

This kit includes both JAR files. 

Alternatively, you can obtain them by either:

	* Checking out the GSF project from GitHub and building the JARs yourself:

		https://github.com/dolfdijkstra/gst-foundation.git

	... or

	* Downloading the pre-compiled JARs from the GSF's website:
	
		http://gst-foundation.org/	

##Support

The Global Site Foundation is supported by the community. Questions and remarks can be send to ips-link@yahoogroups.com. 

Oracle Support does NOT provide support on GSF.

##Support

The Global Site Foundation is supported by the community. Questions and remarks can be send to ips-link@yahoogroups.com. 

Oracle Support does NOT provide support on GSF.

##License

This project is licensed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.

