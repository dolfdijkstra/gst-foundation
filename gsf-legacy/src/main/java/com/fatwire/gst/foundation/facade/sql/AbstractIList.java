/*
 * Copyright 2008 FatWire Corporation. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fatwire.gst.foundation.facade.sql;

import COM.FutureTense.Interfaces.IList;

/**
 * Base IList class supporting navigation and naming.
 * 
 * @author Tony Field
 * @since Aug 13, 2010
 * @deprecated - com.fatwire.gst.foundation.facade and all subpackages have moved to the tools.gsf.facade package
 */
public abstract class AbstractIList implements IList {
    private String name;
    private int currentRow;

    protected AbstractIList(final String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void rename(final String newname) {
        name = newname;
    }

    public final boolean moveTo(final int i) {
        if (1 <= i && i <= numRows()) {
            currentRow = i;
            return true;
        } else {
            return false;
        }
    }

    public final boolean moveToRow(final int how, final int row) {
        if (numRows() == 0) {
            return false;
        }
        switch (how) {
            case first:
                currentRow = 1;
                return true;
            case gotorow:
                return moveTo(row);
            case last:
                currentRow = numRows();
                return true;
            case next:
                if (currentRow == -1) {
                    return false;
                }
                ++currentRow;
                if (currentRow > numRows()) {
                    currentRow = -1;
                    return false;
                }
                return true;
            case prev:
                if (currentRow == -1) {
                    return false;
                }
                --currentRow;
                if (currentRow == 0) {
                    currentRow = -1;
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public final boolean atEnd() {
        return currentRow == -1;
    }

    public final boolean hasData() {
        return numRows() > 0;
    }

    public final int currentRow() {
        return currentRow;
    }

}
