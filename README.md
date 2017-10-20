SingleSelectBar
=====
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui)
[![Github file size](https://img.shields.io/github/size/webcaetano/craft/build/phaser-craft.min.js.svg)]()
[![Download](https://api.bintray.com/packages/supermumu/maven/ui/images/download.svg)](https://bintray.com/supermumu/maven/ui/_latestVersion)
[![](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/supermumu/SingleSelectBar/blob/master/LICENSE.txt)

### New release v1.0.4:

<img src="https://user-images.githubusercontent.com/32517342/31831667-ce90f4ca-b5f6-11e7-97c7-12fb0d11f9b5.gif" width="30%" />

SuperMuMu's ui is a Java library with custom view components.

### SuperMuMu Goals
  * Provide models to convenient/powerful and easy to integrate on your code.

### Gradle
  * To use SuperMuMu-UI in Android
```
dependencies {
    compile 'com.supermumu:ui:1.0.4'
}
```

### How to use?
**Use default style or define customize style via yourself.**

In Java:
```java
SingleSelectBar tabBar = new SingleSelectBar(context);
tabBar.setSelectedColor(Color.RED);
tabBar.setUnselectedColor(Color.BLACK);
tabBar.setTabTextAppearance(R.style.text_appearance);
tabBar.setTabStrokeWidth(4);
```
In Xml:
```xml
<com.supermumu.ui.widget.SingleSelectBoard
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:tabColorSelected="@color/colorPrimary"
        app:tabColorUnselected="@android:color/white"
        app:tabTextAppearance="@style/TextAppearance.CustomText"
        app:tabStrokeWidth="3dp"
        app:tabPressedEffect="true"/>
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
