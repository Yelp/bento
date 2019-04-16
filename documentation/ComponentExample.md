# An Example Bento Component

Let's take a look at an example of how to build a very basic component that just renders some text. Here's an example of a very simple `Component` class:

```kotlin
class ExampleComponent(private val text: String): Component() {
    override fun getCount() = 1

    override fun getPresenter(position: Int) = this

    override fun getItem(position: Int) = text

    override fun getHolderType(position: Int) = ExampleViewHolder::class.java
}
```

Here we can see we've overridden some methods of the abstract `Component` class. Let's take a look at each one:

- `getCount` - Components can be internally made up of a series of items. In our simple case, we only have one item. Each item in the component at each position can have their own presenter, data item and view holder type if we wanted, but usually it's better to break it into different `Component`s unless all items have an identical view holder and presenter.
- `getPresenter` - The presenter is the brains of the component that knows how to respond to user interactions and do other complex state-driven things. In a way, each Bento component is it’s own MVP ecosystem where the `Component` is the Presenter, the `ComponentViewHolder` is the View and the data item is the Model.
- `getItem` - The item is the data that is associated with the component at the specified position. In this case, our data is the `text` that we want to display.
- `getHolderType` - The holder type is a class that is instantiated by the Bento framework through reflection. It's responsible for inflating the component's layout and binding the data item to the view. Let's take a look at our `ExampleViewHolder` class:

```kotlin
class ExampleViewHolder: ComponentViewHolder<ExampleComponent, String>() {

    private lateinit var textView: TextView

    override fun inflate(parent: ViewGroup) =
                    parent.inflate<TextView>(R.layout.example_component_layout)
                        .also { textView = it }

    override fun bind(presenter: ExampleComponent, element: String) {
        textView.text = element
    }
}
```

Much like the view holder pattern we see when using RecyclerViews, Bento's view holders are separated into an inflate and a bind method. Let's take a look at what these methods are doing:

- `inflate` - Here we inflate a layout file which contains nothing but a simple TextView element in its root. We then return that inflated view and also store a reference to it in `textView` so we can use it later when binding data.
- `bind` - This method is called whenever an item in the `Component` is ready to be shown on screen. It is called once for each item as defined by `getCount` in the `Component` class. The `bind` method provides a reference to a presenter and the corresponding data item at the position in the component that this view holder represents. In other words, the `presenter` argument is obtained from calling `getPresenter(i)` at some position i. The `element` argument is also obtained from calling `getItem(i)` for the same position i.
  - NOTE: The bind method is often called as views are recycled in the list and so performance should be a high priority for this method.

Great so now we have a `Component` and a `ComponentViewHolder` that will take some string and bind it to a `TextView` to show the user! How do we actually use the component? We need to create a `ComponentController` that organizes all of the components. For this example, we'll use the simple `RecyclerViewComponentController`. Here it is in an example activity:

```kotlin
class ExampleActivity: AppCompatActivity() {

    private val componentController by lazy {
        RecyclerViewComponentController(recyclerView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        componentController.addComponent(ExampleComponent("Hello World!"))
    }
}
```

Here we create a regular activity whose content view layout is just a RecyclerView with an id of `recyclerView`. We lazily initialize the `ComponentController` the first time it is referenced, and create it by passing in the instance of the RecyclerView. From there we call `addComponent` and pass in a new instance of our `ExampleComponent` with a text string of `Hello World` to display.

Bento also has helper classes to avoid boilerplate code. Our Example component class was actually pretty simple, and so we can write it as a `SimpleComponent`:

```kotlin
class SimpleExampleComponent(
            private val text: String
): SimpleComponent<Nothing>(ExampleViewHolder::class.java) {
        override fun getItem(position: Int) = text
}
```

It's still using our `ExampleViewHolder` from before, but now we don't need to worry about the count or the presenter, and the view holder type is specified in the super constructor.

There are also many variations of components included with Bento, including a `ListComponent` for repeating views, a `PaginatingListComponent` for lazy loading, and even a `CarouselComponent` for collections of components that can be scrolled horizontally. For more examples, keep exploring the wiki!
