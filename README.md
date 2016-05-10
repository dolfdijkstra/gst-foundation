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

The GST Site Foundation accomplishes the following:

1. Defines architectural and design standards for the foundation of a site

2. Standardizes processes for business users

3. Simplifies coding greatly, to the point where much can be provided in common libraries

4. Clearly defines extension points

Documentation for the GST Foundation project is found on [Github Pages](http://dolfdijkstra.github.com/gst-foundation).


[The GST Site Foundation Document](./GST Site Foundation 1.0.3.pdf) defines a simple URL-management and request
handling/request dispatching model that Content Server architects can easily extend and build upon. The initial
emphasis is on a non-rendering foundation that at a high-level provides the following primary benefits:

- Is suitable for use "out of the box" for a large number of sites

- Introduces the notion of a web-referenceable asset, which is accessed via a common controller

- Handles and dispatches requests quickly and efficiently

- Supports easily-constructed vanity URLs for any web-referenceable asset

- Standardizes usage of the Page asset for navigation

- Standardizes meta attributes

- Provides infrastructure for built-in 404 responses for assets as required

- Provides for managed alias assets that can represent any other URL

- Can be cleanly extended to address custom client requirements


    Additionally, this foundation addresses problems that many sites face: addressability of assets by editors, and
    acknowledgement that the URL is more important than the content.  By giving each asset its own URL at the
    discretion of the editor, both problems are solved.

    This model is appropriate for many different solution designs. However there may be some site requirements which
    would make the patterns and conventions of this approach less suitable.  A checklist is included that helps an
    architect to determine if this rendering model is the best approach for the project at hand.

    The Global Site Foundation is initiated in 2010 by Tony Field, Dolf Dijkstra, Michael Sullivan 
    and Suzanne Bourdeaux.

    Developers and Architects are encouraged to build upon and extend this framework with the long-term goal 
    of folding in such enhancements into future versions of the GST Site Foundation.

    August, 2011, The Global Solutions Team

##Documentation

Documentation for the GST Foundation project is found on [Github Pages](http://dolfdijkstra.github.com/gst-foundation).

##Download

The 11.6.0 version of GSF that works with Sites 11gr1 BP1 is on [this repository](https://github.com/dolfdijkstra/mvn-repository/tree/master/releases/com/fatwire/gst/gst-foundation-all).
 

##Support

The Global Site Foundation is supported by the community. Questions and remarks can be send to ips-link@yahoogroups.com. 

Oracle Support does NOT provide support on GSF.

##License

This project is licensed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.

