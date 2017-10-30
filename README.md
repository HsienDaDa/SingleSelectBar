SingleSelectBar
=====
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.supermumu/ui)
[![platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
[![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=16)
[![Github file size](https://img.shields.io/github/size/webcaetano/craft/build/phaser-craft.min.js.svg)]()
[![Download](https://api.bintray.com/packages/supermumu/maven/ui/images/download.svg)](https://bintray.com/supermumu/maven/ui/_latestVersion)
[![](https://img.shields.io/hexpm/l/plug.svg)](https://github.com/supermumu/SingleSelectBar/blob/master/LICENSE.txt)

### New release v1.0.5:

<img src="https://user-images.githubusercontent.com/32517342/32175182-176c87f2-bdbf-11e7-86db-b506610bca3e.gif" width="30%" />

SuperMuMu's ui is a Java library with custom view components.

### SuperMuMu Goals
  * Provide models to convenient/powerful and easy to integrate on your code.

### Gradle
  * To use SuperMuMu-UI in Android
```
dependencies {
    compile 'com.supermumu:ui:1.0.5'
}
```

### How to use?
**Use default style or define customize style via yourself.**

In Java:
```java
SingleSelectBar bar = new SingleSelectBar(context);
bar.setSelectedColor(Color.RED);
bar.setUnselectedColor(Color.BLACK);
bar.setTabTextAppearance(R.style.text_appearance);
bar.setTabStrokeWidth(4);
```
In Xml:
```xml
<com.supermumu.ui.widget.SingleSelectBoard
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:uiColorSelected="@color/colorPrimary"
        app:uiColorUnselected="@android:color/white"
        app:uiPressedEffectStyle="none"
        app:uiRoundCornerRadius="180px"
        app:uiStrokeWidth="3dp"/>
```

**Sets display list of CharSequence(max=5)**

In Java:
```java
SingleSelectBar bar = new SingleSelectBoard(context);
...
bar.setTabs(list);
or
bar.setTabs(list, 3);
```

**Listen all select callback**

In Java:
```java
SingleSelectBar bar = new SingleSelectBoard(context);
...
bar.setOnTabSelectListener(new SingleSelectBar.OnTabSelectListener() {
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
