Anti-café
----

*relax, have fun and pay per second in real time*

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

### License

WTFPL
