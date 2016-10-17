#!/bin/bash
#
# Copyright 2010 FatWire Corporation. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

hash mvn 2>&- || { echo >&2 "The script requires 'mvn' but it's not installed. Aborting."; exit 1; }


if [ -f "cs.jar" ]; then
    if [ "x$1" == "x" ]; then     
        echo -n "Please provide the version of Content Server and press [ENTER]: "
        read  VERSION
    else 
       VERSION=$1 #VERSION captured from the command-line
    fi
    for jar in  assetapi-impl assetapi assetframework assetmaker basic batch \
                catalog cs-core cs cscommerce directory firstsite-filter \
                firstsite-uri flame framework gator gatorbulk ics logging \
                lucene-search MSXML rules sampleasset sseed sserve transformer \
                visitor xcelerate cs-cache-$VERSION
    do
        if [ -f "${jar}.jar" ]; then
            echo "Registering ${jar}.jar"
            mvn -B --quiet install:install-file -Dfile=${jar}.jar -DgroupId=com.fatwire.cs -DartifactId=${jar/-$VERSION} -Dversion=$VERSION -Dpackaging=jar -DgeneratePom=true
        else
            echo "WARNING: File ${jar}.jar not found."
        fi
    done
else
    echo "cs.jar is not found  in your current working directory. Aborting."
    echo "The Content Server jar files are NOT registered in your local maven repository."
    exit 1
fi





