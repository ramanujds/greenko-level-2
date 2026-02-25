## Angular SSE service (RxJS)
Create a small service that opens an `EventSource`, forwards `message` events into an observable, and closes the connection on unsubscribe (component destroy). This pattern is commonly used to adapt SSE into RxJS streams. 

```ts
// sse.service.ts
import { Injectable, NgZone } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SseService {
  constructor(private zone: NgZone) {}

  stream(url: string): Observable<string> {
    return new Observable<string>((observer) => {
      const es = new EventSource(url); // must be http(s) and CORS-enabled if cross-origin

      es.onmessage = (evt) => {
        // Ensure Angular change detection sees updates
        this.zone.run(() => observer.next(evt.data));
      };

      es.onerror = (err) => {
        // EventSource auto-reconnects by default; choose whether to surface error
        this.zone.run(() => observer.error(err));
        // optionally: es.close();
      };

      return () => es.close();
    });
  }
}
```

## Component usage (unsubscribe on destroy)
Subscribe in your component and close the SSE connection when the component is destroyed to avoid leaking open connections. Closing the `EventSource` is the right cleanup mechanism for SSE. [developer.mozilla](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events)

```ts
// log-stream.component.ts
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SseService } from './sse.service';

@Component({
  selector: 'app-log-stream',
  template: `
    <button (click)="start()">Start</button>
    <button (click)="stop()">Stop</button>
    <pre>{{ lines.join('\n') }}</pre>
  `,
})
export class LogStreamComponent implements OnInit, OnDestroy {
  lines: string[] = [];
  private sub?: Subscription;

  constructor(private sse: SseService) {}

  ngOnInit() {
    this.start();
  }

  start() {
    this.sub?.unsubscribe();
    this.sub = this.sse.stream('http://localhost:8080/events').subscribe({
      next: (msg) => this.lines.push(msg),
      error: (e) => this.lines.push('SSE error (see console)'),
    });
  }

  stop() {
    this.sub?.unsubscribe();
    this.sub = undefined;
  }

  ngOnDestroy() {
    this.sub?.unsubscribe();
  }
}
```

