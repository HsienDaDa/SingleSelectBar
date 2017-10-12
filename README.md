SingleSelectBoard
=====
[![Build Status](https://travis-ci.org/com.supermumu/ui.svg?branch=master)](https://travis-ci.org/com.supermumu/ui)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui)
[![Github file size](https://img.shields.io/github/size/webcaetano/craft/build/phaser-craft.min.js.svg)]()
[![Download](https://api.bintray.com/packages/supermumu/maven/ui/images/download.svg)](https://bintray.com/supermumu/maven/ui/_latestVersion)

<img src="https://user-images.githubusercontent.com/32517342/31512085-536bf2ee-afbc-11e7-8703-ed22f349fe13.png" width="30%" />

SuperMuMu's ui is a Java library with custom view components.

### SuperMuMu Goals
  * Provide models to convenient/powerful and easy to integrate on your code.

### Download and Maven
  * To use SuperMuMu-UI in Android
```
dependencies {
    compile 'com.supermumu:ui:1.0.2'
}
```

### How to use?
**Default style selected(color/colorPrimary) and unselected(android:color/white)**

**Define your selected and unselected color.**

In Java:
```java
SingleSelectBoard board = new SingleSelectBoard(context);
board.setSelectedColor(Color.RED);
board.setUnselectedColor(Color.BLACK);
```
In Xml:
```xml
<com.supermumu.ui.widget.SingleSelectBoard
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:colorSelected="@color/colorPrimary"
        app:colorUnselected="@android:color/white"/>
```

**Define your textAooearance.**

In Xml:
```xml
<com.supermumu.ui.widget.SingleSelectBoard
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:boardTextAppearance="@style/TextAppearance.CustomText"/>
```

**Sets display list of CharSequence(max=5)**

In Java:
```java
SingleSelectBoard board = new SingleSelectBoard(context);
...
board.setItems(list);
or
board.setItems(list, 3);
```

**Listen all select callback**

In Java:
```java
SingleSelectBoard board = new SingleSelectBoard(context);
...
board..setOnItemSelectListener(new SingleSelectBoard.OnItemSelectListener() {
            @Override
            public void onSelect(int position, View view) {
                // do something...
            }
        });
```
### License

SuperMuMu is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2008 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
