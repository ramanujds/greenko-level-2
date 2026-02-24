## Typical Angular workspace (top level)

### `node_modules/`
Holds installed dependencies from npm.  
You don’t edit this; it’s recreated by running install commands.

### `package.json`
Your project’s dependency list and scripts (start, build, test, lint).  
When you add libraries (Angular Material, RxJS helpers, etc.), this file changes.

### `package-lock.json` / `yarn.lock` / `pnpm-lock.yaml`
Locks exact dependency versions for consistent builds across machines and CI.  
Commit it so everyone installs the same versions.

### `angular.json`
Angular CLI workspace configuration: build targets, assets, global styles, budgets, environments, and per-project settings.  
If you need to add a global stylesheet, copy assets, configure build options, or tweak serving/building—this is where it usually happens.

### `tsconfig.json` (and friends)
TypeScript compiler configuration. Common ones:
- `tsconfig.json`: base config.
- `tsconfig.app.json`: app compilation.
- `tsconfig.spec.json`: test compilation.

You mainly touch these when enabling strictness, path aliases, or compiler options.

### `.gitignore`
Tells Git what to not commit (node_modules, dist, cache, etc.).

### `README.md`
Project setup notes for your team.

### `dist/` (generated)
Build output after `ng build`.  
Not edited manually.

### `projects/` (optional)
Exists when the workspace contains multiple apps and/or libraries (monorepo style).  
Each project inside has a structure similar to a normal Angular app.

***

## `src/` (main application source)

### `src/index.html`
The single HTML host page.  
Angular mounts your app into a root element here (often something like `<app-root>`).

### `src/main.ts`
Browser entry point that bootstraps the Angular app.  
You’ll touch it rarely (bootstrap changes, enabling providers at startup, etc.).

### `src/styles.css` / `src/styles.scss`
Global styles for the entire application.  
Use for resets, theme variables, shared typography—things that shouldn’t be scoped to one component.

### `src/assets/`
Static files served as-is: images, icons, fonts, JSON mock files, etc.  
If you reference `/assets/...` in templates, it comes from here.

### `src/environments/` (commonly present)
Environment-specific configuration (example: API base URL, feature flags) for dev vs production.  
Angular replaces environment files during production builds.

***

## `src/app/` (where you spend most of your time)

Think of `app/` as “the app’s code.” The structure can vary, but these patterns scale well.

### Root app files (common)
- `app.component.ts` / `.html` / `.scss`  
  The root component (top-level layout shell). Often contains header/footer/router outlet.

- `app.routes.ts` (or `app-routing.module.ts` in older setups)  
  All route definitions. This is where you define lazy loading, guards, and route-level providers (depending on your setup).

- `app.config.ts` (in newer standalone setups)  
  Application-wide configuration/providers (router, HTTP client, interceptors, animations, etc.).

### Recommended folders inside `app/`

#### `core/` (app-wide singletons + infrastructure)
Use for “one instance for the whole app” items:
- Authentication/session services
- Route guards
- HTTP interceptors
- Global error handling
- App-level layout components (sometimes)
- Configuration services

Rule of thumb: core things are used everywhere and are not tied to one feature.

#### `shared/` (reusable building blocks)
Use for reusable, “dumb” pieces:
- UI components (buttons, modals, table wrappers)
- Directives (e.g., autofocus, permission directive)
- Pipes (date/format helpers)
- Common utilities

Rule of thumb: shared components shouldn’t depend on one feature’s business logic.

#### `features/` (or `pages/`)
Where business features live (recommended for maintainability).  
Each feature folder groups its own UI + logic, for example:
- `features/orders/`
- `features/customers/`
- `features/dashboard/`

Inside each feature you might have:
- `components/` (feature-specific components)
- `services/` (feature APIs and domain logic)
- `models/` (feature-specific types)
- `routes/` or `feature.routes.ts` (feature routing, often lazy-loaded)

This “feature-first” structure is the easiest for Spring Boot developers to understand because it mirrors domain modules.

#### `services/` (optional pattern)
Some teams keep services globally in `app/services/`.  
This is fine for small apps, but in larger apps prefer keeping services close to their feature unless it’s truly “core”.

#### `models/` or `types/`
TypeScript interfaces/types for DTOs and view models.  
You can keep them per feature or at app level; per-feature is usually cleaner.

#### `constants/` / `utils/`
App-wide constants and helper functions. Keep them small and focused.

***

## What files you see per component/service (and what they mean)

### Component files
- `something.component.ts`: Component class (state + methods).
- `something.component.html`: Template (view).
- `something.component.scss/css`: Styles scoped to the component (default Angular behavior).
- `something.component.spec.ts`: Unit test.

### Service files
- `something.service.ts`: Injectable class, often wraps API calls or business logic.
- `something.service.spec.ts`: Unit test.

### Routing
- `*.routes.ts` / `*-routing.module.ts`: Route configuration, lazy loading, guards.

***

## Best practices for structuring your app

- Keep `src/` as flat as possible.
- Keep **core** for app-wide infrastructure, **shared** for reusable UI/tools, and **features** for domain modules/screens.
- Prefer grouping code by **feature** rather than by file type when the app grows.
- If something is only used in one feature, keep it inside that feature folder.

