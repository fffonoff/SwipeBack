# SwipeBack
SwipeBack layout allowing you to swipe back just like on iOS

![](img/demo.gif)

### Attributes:

| Attr               | Format    |
| ------------------ | --------- |
| drag_view_id       | reference |
| scrim_color        | color     |
| edge_only          | boolean   |
| edge_size          | float     |
| percent_to_release | float     |

all attributes except `drag_view_id` could be set programmatically

### Usage:

```
<com.fffonoff.swipeback.SwipeBackLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:drag_view_id="@id/draggable"
    app:edge_only="true"
    app:scrim_color="#c000">

    <!-- your root container -->
</com.fffonoff.swipeback.SwipeBackLayout>
```

```
swipeBackLayout.addListener(object : SwipeBackLayout.Listener {
    override fun onClose() {
        close() // e.g. fragmentManager?.popBackStack()
    }
})
```
