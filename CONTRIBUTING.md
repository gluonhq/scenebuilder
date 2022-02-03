## How to Contribute to Scene Builder

Contribution of any form is welcome! Please see workflows below on how you can contribute to the project.

## Feature Request Workflow

1. Open a [discussion](https://github.com/gluonhq/scenebuilder/discussions) to suggest a feature backed up by a use case.
2. A maintainer will confirm the feature is beneficial for Scene Builder and will convert the discussion into a feature-request issue.

## Contribution Workflow

1. Pick an unassigned [open issue](https://github.com/gluonhq/scenebuilder/issues).
2. Comment on it to say you would like to contribute to fixing it and propose a plan (if appropriate).
3. A maintainer will confirm the issue is valid and can be assigned to you for a fix.
4. You produce a Pull Request following the Standards below. It is expected that you will also provide accompanying tests.
5. Once you tick all check boxes on the Pull Request template, it will be reviewed by a maintainer or a community member.
6. Once the Pull Request is approved (`minor` requires at least 1, `major` requires at least 2), it will be ready for a squashed merge.

## QA Test Workflow

1. Grab an [early release package](https://github.com/gluonhq/scenebuilder/releases/tag/early-access).
2. Test newly merged features since the latest stable version, paying attention to the functionality of existing features, OR proof read the [public documentation](https://github.com/gluonhq/scenebuilder/wiki) for errors, ambiguities and typos.
3. Report any bugs, odd behavior, or inconsistencies to the maintainers as appropriate.

## Coding Standards

* Any code contribution should follow [the OpenJFX guidelines](https://github.com/openjdk/jfx/blob/master/CONTRIBUTING.md#coding-style-and-testing-guidelines).
* Any Pull Request should follow the provided template.

## Pull Request Standards

The project uses the following Pull Request message guidelines, based on [Conventional Commits 1.0.0](https://www.conventionalcommits.org/en/v1.0.0/):

* **build: Message** -- change affects the build system, configuration files, scripts, or external dependencies
* **docs: Message** -- change affects documentation **only**, including LICENSE, CONTRIBUTING, README
* **feat: Message** -- change adds a new or modifies an existing feature
* **fix: Message** -- change fixes a bug
* **perf: Message** -- change is related to performance
* **refactor: Message** -- change cleans up or restructures code, including formatting only changes
* **test: Message** -- change that adds new or updates existing tests and mostly affects the `test` package

For concrete examples, see [latest merged requests](https://github.com/gluonhq/scenebuilder/commits/master).