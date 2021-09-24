# Contributing to the project

## Restricted branches
- `develop` - all the implemented features which are done and deployed
- `master` - stable version deployed

## Workflow

1. `git pull` on the `develop` branch to have the newest version for the base
2. Create a feature branch when you start to work on a story and commit your changes to this
3. Squashing your commits before pushing your code to the remote repository   
4. When the feature is done, create a Pull Request from the `ZM-X` to `develop`, follow the guidelines
5. When the PR is approved, merge it
6. After sprint review, remove the accepted feature branches

## Resolving conflicts

1. Checkout to `develop` branch and make a `git pull` to get the newest version
2. Checkout to your feature branch and `git rebase develop` to rebase to the new `develop`
3. Resolve all the conflicts in the code and run all the `tests` to be sure nothing is broken after resolving
4. After resolving them add the changes with `git add .` or `git add file_name`
5. Continue the rebasing with `git rebase --continue` or abort it with `git rebase --abort` to discard all the changes
6. If finished with rebasing `git push -f` to refresh the PR

## Commit messages

For one PR, you should have 1 commit in total that should follow this pattern:
`ZM-X name of the ticket`  
If you have multiple commits, make an interactive rebase to squash them into one.

1. Check how many commits you have on your feature branch with `git log`. You can exit the log with `Q`
2. Start the interactive rebase with `git rebase -i HEAD~"number of your commits`
3. Mark the top commit with `pick` and the rest you want to merge in with `s` or `squash`
4. Keep or modify the commit message you want to have for your commit and comment out the rest
5. `git push -f ` to refresh your PR

## Pull Request guidelines

- From `feature_branch` to `develop`: add all developers and PM as reviewers, 3 approves needed for merging
- From `develop` to `master`: this is managed by the PM
