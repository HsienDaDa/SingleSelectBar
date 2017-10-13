SingleSelectBoard
=====
[![Build Status](https://travis-ci.org/com.supermumu/ui.svg?branch=master)](https://travis-ci.org/com.supermumu/ui)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui)
[![Github file size](https://img.shields.io/github/size/webcaetano/craft/build/phaser-craft.min.js.svg)]()
[![Download](https://api.bintray.com/packages/supermumu/maven/ui/images/download.svg)](https://bintray.com/supermumu/maven/ui/_latestVersion)

### Next release v1.0.3 preview:

<img src="https://user-images.githubusercontent.com/32517342/31527961-235fc886-b003-11e7-8b9a-4fde35975dfc.gif" width="30%" />

SuperMuMu's ui is a Java library with custom view components.

### SuperMuMu Goals
  * Provide models to convenient/powerful and easy to integrate on your code.

### Download and Maven
  * To use SuperMuMu-UI in Android
```
dependencies {
    compile 'com.supermumu:ui:1.0.3'
}
```

### How to use?
**Use default style or define customize style via yourself.**

In Java:
```java
SingleSelectBoard board = new SingleSelectBoard(context);
board.setSelectedColor(Color.RED);
board.setUnselectedColor(Color.BLACK);
board.setTextAlignment(R.style.text_appearance);
board.setBoardStrokeWidth(4);
```
In Xml:
```xml
<com.supermumu.ui.widget.SingleSelectBoard
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:colorSelected="@color/colorPrimary"
        app:colorUnselected="@android:color/white"
        app:boardTextAppearance="@style/TextAppearance.CustomText"
        app:boardStrokeWidth="3dp"/>
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
Copyright 2017 by SuperMuMu

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
