## “Same, but different” 

## 1) Interfaces: like Java contracts, but for object shapes
In Java, an interface defines methods a class must implement; in TS it often describes the *shape* of plain objects (like JSON).

```ts

interface UserDto {
  id: number;
  name: string;
  email?: string; // optional
}

// Works with plain objects (no "new" required)
const u: UserDto = { id: 1, name: 'Asha' };
```

## 2) Classes & constructor injection: feels like Spring, but lighter
Angular services and components are classes, and you’ll often inject dependencies via the constructor, similar to Spring DI in spirit. TypeScript supports access modifiers and “parameter properties” (declaring+initializing fields directly in the constructor). [typescriptlang](https://www.typescriptlang.org/docs/handbook/classes.html)

```ts
class ProductService {
  constructor(private baseUrl: string) {} // private field auto-created

  getUrl(): string {
    return this.baseUrl;
  }
}
```

## 3) Generics: very similar to Java, used everywhere in typed APIs
You’ll use generics to keep responses and collections type-safe, comparable to `List<Product>` in Java. [typescriptlang](https://www.typescriptlang.org/docs/handbook/2/generics.html)

```ts
interface ApiResponse<T> { data: T; requestId: string; }

function unwrap<T>(res: ApiResponse<T>): T {
  return res.data;
}
```

## 4) Null/undefined: the big mindset shift from Java
JS has both `null` and `undefined`, and Angular templates/data often start “empty” until HTTP resolves; TypeScript `strict` (including `strictNullChecks`) tightens this.

```ts
// With strictNullChecks, this is explicit:
let selectedId: number | null = null;

function setSelected(id?: number) {
  selectedId = id ?? null; // handle undefined
}
```

## 5) Access control: TS compile-time vs JS runtime private
TypeScript’s `private`/`protected` are checked by the TS compiler. JavaScript also has runtime-enforced private fields using `#name` (different from TS `private`). 

```ts
class A {
  private token = 'ts-private'; // TS-level privacy
  #realSecret = 42;             // JS runtime private field [web:20]
}
```
