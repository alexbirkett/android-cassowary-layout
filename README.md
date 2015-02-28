Cassowary Layout for Android
========================


[![Demo video](http://share.gifyoutube.com/vQkB6M.gif)](http://www.youtube.com/watch?v=_FYroNxqFqo)

[Watch video on YouTube](http://www.youtube.com/watch?v=_FYroNxqFqo)


## Contact

You can reach me on twitter as [@alexbirkett](https://twitter.com/alexbirkett). 


## Dependencies

###Cassowary
This project depends on [the pybee cassowary fork](https://github.com/pybee/cassowary-java).

#Examples

##XmlLayoutDemo
The XmlLayoutDemo demonstrates how cassowary constraints can be defined in an XML file.

###Layout

The layout will be familiar to users of [RelativeLayout](http://developer.android.com/reference/android/widget/RelativeLayout.html) and [LinearLayout](http://developer.android.com/reference/android/widget/LinearLayout.html). The cassowary layout defined in ```activity_xml_layout_demo.xml``` has four children, each assigned ids and colors. The constratins to be used are speficied by the ```cassowary:constraints``` attribute.

```
<no.agens.cassowarylayout.CassowaryLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:cassowary="http://schemas.android.com/apk/res-auto"
    android:id="@+id/layout"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="no.agens.cassowarylayoutdemo.XmlLayoutDemo"
    cassowary:constraints="@array/xml_demo"
    android:padding="@dimen/default_padding">

    <View
        android:id="@+id/red"
        android:background="@color/red"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <View
        android:id="@+id/green"
        android:background="@color/green"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <View
        android:id="@+id/blue"
        android:background="@color/blue"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <View
        android:id="@+id/purple"
        android:background="@color/purple"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

</no.agens.cassowarylayout.CassowaryLayout>
```

### Constraints
In the demo app, the constraints are defined in ```values/constraints.xml```

```
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
```

We can see that the postitions of the views are functions of the container width and height. For example, blue.left is defined as 1/10 of the container width.

Note that the view width and height are functions of the container width and height too. ```blue.width == (container.width + container.height) / 10``` (The bigger the screen, the bigger the child views)

How it looks:
![xml demo](https://github.com/alexbirkett/android-cassowary-layout/raw/master/screenshots/XmlLayoutDemo.png)



## Who's behind this?

Agens.no a company situated in Oslo, Norway.


[![Agens | Digital craftsmanship](http://static.agens.no/images/agens_logo_w_slogan_avenir_small.png)](http://agens.no/)
