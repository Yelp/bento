<img src="./documentation/images/bento_logo.png" width="80%"/>

## A delicious framework for building modularized Android user interfaces, by Yelp.

![Maven Central](https://img.shields.io/maven-central/v/com.yelp.android/bento.svg)
[![Build Status](https://travis-ci.org/Yelp/bento.svg?branch=master)](https://travis-ci.org/Yelp/bento)
[![Twitter](https://img.shields.io/badge/Twitter-@YelpEngineering-blue.svg)](https://twitter.com/YelpEngineering)
[![License](https://img.shields.io/badge/license-Apache2.0%20License-orange.svg)](LICENSE)

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

We're compiling a list of examples of how to use Bento. Here's what we have currently and some of the examples we plan to add in the future:

- [SimpleComponent](documentation/ComponentExample.md)
- [ListComponent](documentation/ListComponentExample.md)
- [Component User Interaction](documentation/UserInteractionExample.md)
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

#### 0. Requirements

There are a few requirements to compile Bento.

- Python is needed for the [pre-commit](https://pre-commit.com/) hooks to run
- Java and Kotlin are needed to actually compile the project
- Android Studio (we recommend >= 3.5.0)

#### 1. Get the code

Fork the this repository, and then clone the code to your local machine.

#### 2. Prepare the git hooks

Once you have the repo on your machine, run the following command from the root of the project.

```
$ make install-hooks
```

These git hooks will make sure you're not committing private keys to the repository. In the future we might add more functionality.

#### 3. Create a new branch

We usually like branches to be named:

- `username/issue-number/what-it-do`
- `targo/24/fix-item-range`

#### 4. Build, change, test, commit

Repeat as necessary. The project should build from within Android Studio and if it doesn't, see the help section below. You should also be able to compile and run the `bento-sample-app` to test your changes.

You can also run `./gradlew publishToMavenLocal` to publish the package to your local maven repo on your machine. From there you can add `mavenLocal()` before other maven repositories. That way your project can load in the version of Bento you're working on.

We follow the [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) specification, and you should write what your changes are about in a clear commit message.

#### 5. Push your changes and open a pull request

First, squash your commits if you have multiple, and make sure your commit message follows the [conventional commits](https://www.conventionalcommits.org/en/v1.0.0/) convention.
Push your branch with the new commits to your cloned repo, then open a pull request through GitHub. Once Travis CI gives it a green light along with a member of our team, it will be merged.

If your reviewer asks for some changes, address the issue and then make sure to amend your previous commit - there should be only one commit per pull request. Push your changes, repeat until you get a green light.

## Making a release
We switched to using [standard-version](https://github.com/conventional-changelog/standard-version) to handle our releases. standard-version is a utility for versioning using [semver](https://semver.org/) and CHANGELOG generation powered by Conventional Commits.

If you are one of the members and must release a new version, then check its documentation. You will need to install `npm` if you don't have it. (We recommand using `homebrew` to do so.)

With `npm` install, install standard-version globally: `npm i -g standard-version`

From master, run `standard-version`.

`standard-version` will take care of
* Detect the previous version tag (based on git tags)
* Infer the next version number, based on previous standard commit messages. A breaking change will trigger a major bump, a new feature will trigger a minor bump, and a fix will trigger a patch bump.
* Update the CHANGELOG.md file with the commit messages, as well as the [GlobalDependency.kt](buildSrc/src/main/java/com/yelp/gradle/bento/GlobalDependencies.kt) file with the new version number.
* Create a new commit with these change, and tag it with the version number.

The rest has to be done manually: check that the commit looks good (proper changelog, version number, tag), then push both commit and tags to master.

## Help

- Open a GitHub issue and tag it with the `Question` tag
- Ask a question on StackOverflow and tag it with `bento` and `android`

## License

Apache 2.0 - Please read the [LICENSE](LICENSE) file.
