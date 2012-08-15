#!/bin/bash
VERSION=1.4-SNAPSHOT
echo building jars and site
mvn -q clean install
mvn -q site
mvn site:stage -DstagingDirectory=/tmp/gsf-site

archive=`pwd`/target/gst-foundation-$VERSION.tgz 

(cd gsf-assembly/target  && tar -czf $archive gst* -C /tmp/ gsf-site )

echo done packaging gst-foundation
#  mvn org.apache.maven.plugins:maven-dependency-plugin:2.5:tree -DoutputType=graphml -DoutputFile=dependency.graphml
#   yEd ./dependency.graphml 
# find . -name dependency.graphml -exec rm {} \;


# find newer versions for plugins
#mvn versions:display-plugin-updates

#versions:dependency-updates-report
#  Generates a report of available updates for the dependencies of a project.

#versions:display-dependency-updates
#  Displays all dependencies that have newer versions available.


