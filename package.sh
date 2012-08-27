#!/bin/bash
VERSION=11.0-SNAPSHOT
echo downloading all artifacts
mvn -q dependency:go-offline
echo building jars and site
mvn -o -q -pl gsf-build-tools install 
mvn -o -q clean install site
if [ -d /tmp/gsf-site ] ; then rm -Rf /tmp/gsf-site ;fi
mvn -o site:stage -DstagingDirectory=/tmp/gsf-site

archive=`pwd`/target/gst-foundation-$VERSION.tgz 

(cd gst-foundation-all/target  && tar -czf $archive gst* -C /tmp/ gsf-site )

echo done packaging gst-foundation
# to get a graphical dependency tree: http://www.summa-tech.com/blog/2011/04/12/a-visual-maven-dependency-tree-view/
#  mvn org.apache.maven.plugins:maven-dependency-plugin:2.5:tree -DoutputType=graphml -DoutputFile=dependency.graphml
#   yEd ./dependency.graphml 
# find . -name dependency.graphml -exec rm {} \;


# find newer versions for plugins
#mvn versions:display-plugin-updates

#versions:dependency-updates-report
#  Generates a report of available updates for the dependencies of a project.

#versions:display-dependency-updates
#  Displays all dependencies that have newer versions available.

#dependency:list-repositories


# Handing license header files

# The license is defined in the gsf-build-tools project. If the header needs to change it needs to be done there
# The LicenseHeader.txt does not contain the copyright statement, as this can be different for each file. This file is used
# to trick the license plugin and not barf over different copyright statements. 
# you can issue mvn license:format -Dcompany="My Corporation" to add header files with a copyright statement to all files that do not have a license header.
