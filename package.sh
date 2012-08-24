#!/bin/bash
VERSION=11.0-SNAPSHOT
echo building jars
mvn -q clean install
echo building site
mvn -q site
if [ -d /tmp/gsf-site ] ; then rm -Rf /tmp/gsf-site ;fi
mvn site:stage -DstagingDirectory=/tmp/gsf-site

archive=`pwd`/target/gst-foundation-$VERSION.tgz 

(cd gsf-assembly/target  && tar -czf $archive gst* -C /tmp/ gsf-site )

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
