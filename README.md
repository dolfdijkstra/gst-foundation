-
- Copyright 2010 FatWire Corporation. All Rights Reserved.
-
- Licensed under the Apache License, Version 2.0 (the "License");
- you may not use this file except in compliance with the License.
- You may obtain a copy of the License at
-
- http://www.apache.org/licenses/LICENSE-2.0
-
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
- limitations under the License.
-

#GST Site Foundation Overview

The GST Site Foundation accomplishes the following:

1. Defines architectural and design standards for the foundation of a site

2. Standardizes processes for business users

3. Simplifies coding greatly, to the point where much can be provided in common libraries

4. Clearly defines extension points



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
  
- [Architecture specification document](./GST Site Foundation 1.0.3.pdf) - the core specification.

- [ Introducing GST Site Foundation](http://img.en25.com/Web/Fatwire/TTRNG%20-%20Introducing%20GST%20Site%20Foundation.pdf) - Interact 2010 User Conference Presentation.

- [ SEO with CS and the GSF](http://img.en25.com/Web/Fatwire/SEO%20Optimization%20Using%20CS%20-%20condensed.pdf) - Interact 2010 User Conference Presentation.

- [JavaDoc API Reference](./apidocs/index.html) - toolkit API.
    
- [JSP Tag library](./gsf-taglib/taglibvalidation.html) - The JSP tag lib documentation.

- [GSF Installation ReadMe](./InstallGuide.html) - Minimalistic gide for installing and configuring the GSF toolkit

- Functioning Microsite Sample {{./GST-Dispatcher.xml}} and {{./Microsite.jsp}}.
    
- MVC and Action Support [ in the Groovy project documentation](./gsf-groovy).
    
- [ GSF Beginner Guide](./GSF_Beginner_Guide_v1.3.pdf) - A down-under introduction to GSF, by Ram Sabnavis.

- [GSF Developer's Guide](./GSF Developers Guide 1.0.docx) - (Partially outdated) Step-by-step instructions for installing and using the GSF toolkit.

##Download

    The GSF is packaged into a single, downloadable bundle.  

-  Pre-compiled Jar {{http://www.nl.fatwire.com/maven2/com/fatwire/gst/gst-foundation-all/}}

     []
     

##Installation

    Installing the GSF is simple, but affects multiple components.  The [minimalistic guide](./InstallGuide.html)
    should get you up and running quickly.

    More detailed information can be found in the somewhat outdated [GSF Developer's Guide](./GSF Developers Guide 1.0.docx).

##Support

    The Global Site Foundation is supported by the community. Questions and remarks can be send to ips-link@yahoogroups.com. 
##Building

  The core components must be downloaded from [GitHub](https://github.com/dolfdijkstra/gst-foundation).


- [Maven version 2.2.1](http://maven.apache.org/) should be used to build the GSF. Earlier and later versions of maven might not work (YMMV).

- Before building the Content Server jars files must be addedd to your local maven repository with the [windows](./build/maven-contentserver-jars.bat) or [UNIX](./build/maven-contentserver-jars.sh) script.

       This script is best executed from the Content Server <WEB-INF/lib> directory.

       At the top of the script is a version number stated, you will need to provide the correct version number of the version of Content Server in the WEB-INF/lib directory.

       You will need to compare this to the version number  in the gsf-cs-all.pom file: `<fatwire.contentserver.version>7.6.0</fatwire.contentserver.version\>`.

       If the version number do no match you can build by providing the version number on the command-line:

       `mvn install -Dfatwire.contentserver.version=7.6.0`.


- Execute `mvn install` to build the jars and install them in your local repository.

##License
This project is licensed under the [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.

