#!/bin/bash
set -o nounset
set -o errexit

VERSION=11.6.0-SNAPSHOT
if [ ! -d $HOME/.m2/repository/com/fatwire/gst/gst-foundation-all ] ;
then 
   echo The GSF artifacts seem not the be present in your maven  maven repository, this is expected if you are building for the first time on this computer
   echo Starting initial build
   echo
   (cd gsf-build-tools && mvn -q -Dmaven.test.skip=true install && cd .. && mvn install)
   echo  finished initial build
fi
echo downloading all artifacts
mvn -q dependency:go-offline
echo building jars and site
#mvn -o -q clean install site
mvn -o -q clean install
tmpLocation=/tmp/gsf-$VERSION
if [ -d "$tmpLocation" ] ; then rm -Rf "$tmpLocation" ;fi
mkdir "$tmpLocation"
#mvn -o site:stage -DstagingDirectory="$tmpLocation/site"

if [ ! -d `pwd`/target ] ; then mkdir `pwd`/target ;fi
archive=`pwd`/target/gst-foundation-$VERSION
 
echo adding primary artifacts to kit
cp -R gst-foundation-all/target/gst* "$tmpLocation"
echo copying license to kit
cp LICENSE "$tmpLocation"
echo compressing
(cd /tmp && tar -czf ${archive}.tgz gsf-* && zip -r ${archive}.zip gsf-*) 

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

# quick check if a copyright statement is present
# find . -name '*.java'  ! -exec egrep -q 'Copyright .* All Rights Reserved' {} \; -ls


#interacting with the webdav server

#downlaod
#wget --user=someone@oracle.com --ask-password https://stbeehive.oracle.com/content/dav/st/WebCenter%20Sites%20GSF/Documents/gst-foundation-11.6.0-SNAPSHOT.tgz


# upload
#curl -T target/gst-foundation-11.6.0-SNAPSHOT.tgz  --user someone@oracle.com https://stbeehive.oracle.com/content/dav/st/WebCenter%20Sites%20GSF/Documents/gst-foundation-11.6.0-SNAPSHOT.tgz


