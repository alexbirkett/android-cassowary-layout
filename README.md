Cassowary Layout for Android
========================


[![Demo video](http://share.gifyoutube.com/vQkB6M.gif)](http://www.youtube.com/watch?v=_FYroNxqFqo)

[Watch video on YouTube](http://www.youtube.com/watch?v=_FYroNxqFqo)


## Quickstart

Add the jCenter repo to the `build.gradle` in the root of your project

    repositories {
        jcenter()
    }

Add the "Cassowary Layout for Android" and Cassowary dependencies to the `build.gradle` for your app or library.

    dependencies {
        // your other dependencies
        compile 'no.agens:cassowary-layout:0.0.1@aar'
        compile 'org.pybee:cassowary:0.0.1'
    }

#Examples

The example app included in this repo contains a variety of different usage examples.

## Hello World

###Layout

The layout will be familiar to users of [RelativeLayout](http://developer.android.com/reference/android/widget/RelativeLayout.html) and [LinearLayout](http://developer.android.com/reference/android/widget/LinearLayout.html). The cassowary layout defined in `activity_xml_layout_demo.xml` in the example app has four children, each assigned ids and colors. Constraints are used to describe the position of the children. The constraints are specified by the `cassowary:constraints` attribute.

```
<?xml version="1.0" encoding="utf-8"?>
<no.agens.cassowarylayout.CassowaryLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:cassowary="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="no.agens.cassowarylayoutdemo.XmlLayoutDemo"
    cassowary:constraints="@array/xml_demo">

    <TextView
        android:id="@+id/blue"
        android:background="#FF0000FF"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Cassowary"
        android:textColor="#FFF"/>

    <TextView
        android:id="@+id/green"
        android:background="#FF00FF00"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Layout"
        android:textColor="#FF0"/>

    <TextView
        android:id="@+id/red"
        android:background="#FFFF0000"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello"
        android:textColor="#FFF"/>

    <TextView
        android:id="@+id/purple"
        android:background="#FFFF00FF"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="World"
        android:textColor="#FFF"/>

</no.agens.cassowarylayout.CassowaryLayout>
```

### Constraints
In the demo app, the constraints are defined in ```values/constraints.xml```

```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="xml_demo">
        <item>blue.left == container.width / 10</item>
        <item>blue.top == container.height / 10</item>
        <item>blue.width == (container.width + container.height) / 10</item>
        <item>blue.height == blue.width</item>

        <item>green.right == container.width - (container.width / 10)</item>
        <item>green.top == container.height / 10</item>
        <item>green.width == blue.width</item>
        <item>green.height == blue.height</item>

        <item>red.left == container.width / 10</item>
        <item>red.bottom == container.height - (container.height / 10)</item>
        <item>red.width == blue.width</item>
        <item>red.height == blue.width</item>

        <item>purple.right == container.width - (container.width / 10)</item>
        <item>purple.bottom == container.height - (container.height / 10)</item>
        <item>purple.width == blue.width</item>
        <item>purple.height == blue.width</item>

    </string-array>
</resources>
```

We can see that the positions of the views are functions of the container width and height. For example, blue.left is defined as 1/10 of the container width.

Note that the view width and height are functions of the container width and height too. ```blue.width == (container.width + container.height) / 10``` (The bigger the screen, the bigger the child views)

How it looks:
![xml demo](https://github.com/alexbirkett/android-cassowary-layout/raw/master/screenshots/XmlLayoutDemo.png)


## Contact

You can reach me on twitter as [@alexbirkett](https://twitter.com/alexbirkett).


## Dependencies

###Cassowary
This project depends on [the pybee cassowary fork](https://github.com/pybee/cassowary-java).

## Who's behind this?

Agens.no a company situated in Oslo, Norway.


[![Agens | Digital craftsmanship](http://static.agens.no/images/agens_logo_w_slogan_avenir_small.png)](http://agens.no/)
