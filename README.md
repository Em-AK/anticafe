Anti-café
----

*relax, have fun and pay per second in real time*

[![Netlify Status](https://api.netlify.com/api/v1/badges/e5e08124-dcc3-4bbd-a4bc-30eb7c262fd8/deploy-status)](https://app.netlify.com/sites/goofy-mahavira-0ddc1f/deploys)

### Requirements

- node (v6.0.0+)
- yarn
- any Java SDK (Version 8 or higher)

### Develop

Run in development:

```bash
yarn
yarn html
yarn watch
```

`shadow-cljs` will be installed in `node_modules/` when you run `yarn`.

`:dev-http` specifies that `target/` will be served at http://localhost:8080 .

### REPL

After page is loaded, you may also start a REPL connected to browser with:

```bash
yarn repl
```

### Connect Fireplace.vim to REPL server

When watch compilation is running, open a cljs file in vim and

```
:CljEval (shadow/repl :dapp)
```

### Release

Compile with optimizations with `build` sub-command:

```bash
yarn build # also serving compiled target/ on http://localhost:8080
```

Continuously deployed by Netlify on each merge into `master` with the following
config:

```
Build command: yarn deploy
Publish directory: target
```

with the following ENV variables:

```
NETLIFY_USE_YARN=true
NODE_VERSION=12
```

and all [assets optimizations features](https://docs.netlify.com/site-deploys/post-processing/#post-processing-features) turned on.

Basically `main.js` is served by Netlify's CDN at a fingerprinted url based
on a hash of the file content, with a cache expiry of ~1 year.

This means that:
* a user visiting the dapp from an online device will always get
the latest release of the code
* a returning user will get the cached version if no release have
been published since the last visit.

### License

WTFPL
