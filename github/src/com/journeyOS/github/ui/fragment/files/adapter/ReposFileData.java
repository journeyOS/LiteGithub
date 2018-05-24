/*
 * Copyright (c) 2018 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.github.ui.fragment.files.adapter;

import com.journeyOS.base.adapter.BaseAdapterData;
import com.journeyOS.github.R;

public class ReposFileData implements BaseAdapterData {

    public String name;

    public int size;

    public String path;

    public boolean isFile;

    public boolean isDir;

    public String url;

    public String htmlUrl;

    public String downloadUrl;

    @Override
    public int getContentViewId() {
        return R.layout.layout_item_file;
    }

    @Override
    public String toString() {
        return "ReposFileData{" +
                "name='" + name + '\'' +
                ", size=" + size +
                ", path='" + path + '\'' +
                ", isFile=" + isFile +
                ", isDir=" + isDir +
                ", url='" + url + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }

}