# Bento

## A delicious framework for building modularized Android user interfaces, by Yelp.

[![License](https://img.shields.io/badge/license-Apache2.0%20License-orange.svg)](LICENSE) [![Twitter](https://img.shields.io/badge/Twitter-@YelpEngineering-blue.svg)](https://twitter.com/YelpEngineering)

Bento is a framework inspired by [Epoxy](https://github.com/airbnb/epoxy) for building complex, modularized Android user interfaces. By leveraging the mechanics and best practices behind RecyclerViews on Android, Bento makes it easy to compose various resusable visual components into a single screen. At Yelp, we've been using Bento for our most critical and complex screens in both our consumer and business owner Android applications.

## How does it work?

Most Android apps that have a list-based user interface use a RecyclerView to display their views. At a basic level, the RecyclerView works by referencing an ordered list of data and creating a view on screen for each item of data in that list. That works really well if your list consists of homogenous data types, but can quickly become unruly when you need to manage an unbounded number of data and view types to be rendered in a list. It also becomes an issue if you need to use the same view type in a different type of user interface other than a RecyclerView, such as a ViewPager or a ListView.

Bento aims to fix these issues by providing a framework to manage the the complexity of handling different view types and the dynamic position of each view in the list. Bento can also be used to manage views in other parent view types such as ViewPagers and ListViews all while keeping the benefits of RecyclerView best practices like view holders and view recycling.

Bento groups different view types and the logic associated with displaying and interacting with those view types into "Components". A `Component` can be anything from a simple text view to a horizontal carousel comprised of other components.

At its core, a `Component` is a self-contained element that generates a data item. An associated `ComponentViewHolder` class will inflate a view and bind the data item provided to the inflated view. The view holder will also typically bind the `Component` to the view to handle any user interactions.

We can also create groupings of different components using a `ComponentGroup`, which is also a `Component` itself, to keep logical groupings of components together in the list.

The order of a `Component` in its parent view relative to other components is determined by the `ComponentController`. This interface is the magic soy sauce that allows us to add, remove and insert components dynamically into the ordering as if we were manipulating values in a simple list data structure. It provides an abstraction we can use to apply this functionality to different view types such as RecyclerView, ListView and ViewPager. For example the `RecyclerViewComponentController` handles the complex coreography of communicating with the RecyclerView class on determining spans and positions and makes it very simple to manage diverse sets of components in a list.

The Bento framework makes it easy to break down complex interfaces into a set of easy to understand, modular, dynamic and testable components.

## Features

- Modular - Independent sections of a screen should be, well, independent. Bento components are self-contained.
- Testable - The separation of concerns between a component's parts makes it easy to write unit tests for presenter logic and espresso tests for view binding logic, enabling complete test coverage.
- Reusable - Bento components can be shared across screens in your app, making code sharing simple.
- Progressive - No need to rewrite your app from scratch. Bento components can be integrated progressively into your existing application. It even works with ListViews.
- Scalable - Perfect for large screens with a long list of heterogeneous views. View recycling also helps maximize performance.
- Low build overhead - No annotation processing means fast build times.

## Examples

TODO(SEARCHUX-7508): Three or four examples, plus a link to a Bento Cookbook with lots of examples.

- SimpleComponent
- ListComponent
- Complex Custom Component
- Component with Lanes
- Inter-Component Communication
- Testing a Component
- Testing a screen with bento-testing
- Integrating Bento into an Existing Project

## Installation

Bento can be setup with Gradle:

```groovy
// Top level build.gradle
allprojects {
	repositories {
		mavenCentral()
	}
}

// Module level build.gradle
dependencies {
    implementation "com.yelp.android:bento:<version-number>"
    androidTestImplementation "com.yelp.android:bento-testing:<version-number>"
}
```

## Contributing

We highly encourage contributions from the community, even if you've never contributed to an open source project before! If you see an issue but don't have time to fix it, open an issue in this repository.

### Steps for Contributing

- TODO(SEARCHUX-7506): Steps for how to checkout the code, run tests, submit a pull request and deploy.

## Help

- Open a GitHub issue and tag it with the `Question` tag
- Ask a question on StackOverflow and tag it with `bento` and `android`

## License

Apache 2.0 - Please read the [LICENSE](LICENSE) file.
