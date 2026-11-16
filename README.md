# Online Barter Trader

An Android marketplace for exchanging goods without money — list an item, browse what
others have posted, request a trade, chat, and rate the exchange afterward.

Built by **seven developers over nine weeks** for CSCI 3130 (Intro to Software Engineering)
at Dalhousie University, Summer 2026. Three iterations, ~12,800 lines of Java and XML,
371 commits. Published with the team's agreement.

**Stack** — Java · Android · Firebase (Realtime Database, Auth) · Google Maps · Gradle
**Testing** — JUnit · Espresso · Mockito · GitLab CI
**Process** — Agile/XP, TDD, pair programming, merge requests with peer review

---

## Who wrote what

This is a team repository. Of the 371 commits, **58 are mine** (Ka Won Jung / KaWon Jung in
`git shortlog`). My scope was 5 user stories and 2 additional issues — 35 story points.

### Owned end to end

**US18 — Rating system** (`#67`, `#86`) · demoed for Iteration 3
- `Rating` model, `UserRatingActivity`, rating dialog and layouts
- `FirebaseManager.getRatings()` with a custom `RatingValueEventListener` interface,
  and `submitRating()`
- Average-rating calculation joining two Firebase nodes (`ratings` + `transactions`),
  keeping the running average correct
- Full app integration: "Rate this Exchange" on `transaction_item.xml`, a 1–5 selector
  dialog, `TransactionAdapter.showRatingDialog()` / `submitRating()`, a `sellerId` field
  on `Transaction`, and a "My Ratings" entry point in `MyProductsActivity`

**US7 — View product requests** (`#31`)
- `ViewRequestsActivity`, `Request` model (9 fields), `activity_view_requests.xml`
- `FirebaseManager.submitRequest()` / `updateRequestStatus()`
- Requester-details dialog with Accept/Decline controls, added after client feedback
- Merged via MR `#21`

### Acceptance tests written before the implementation existed

**US15 — Chat** (`#64`) — 3 Espresso tests written before any implementation, which forced
the interface contract up front: a `Chat` object as the `CHAT` intent extra,
`MessageSender.sendMessage()` returning a `Task` so the failure path was observable, and
fixed view IDs. Later added 2 notification tests.

**US6 — Request product** (`#30`) — acceptance tests.

**Password reset** (`#6`, `#7`) — test implementation and debugging, plus Firebase setup.

All **20 acceptance tests** across these stories pass. I also repaired 3 failing tests that
were blocking the shared `develop` branch, and diagnosed one CI failure as a Gradle plugin
resolution problem (`foojay-resolver-convention`) rather than a code defect.

---

## Design patterns used

Applied across the codebase by the team, documented in the iteration reports:

| Pattern | Where |
|---|---|
| Singleton | `FirebaseManager`, with a test-installable stub |
| Observer | `IChatObserver` / `IMessageObserver`, `MessageNotifier`, `ChatNotifier` |
| Facade | `ChatAdapterChatObserver` wrapping the RecyclerView adapter, `ChatStorage`, `ChatNotifier` |
| Adapter | `ProductSearchResultAdapter`, `TransactionAdapter` |

SOLID principles (SRP, OCP, ISP) applied and documented. Javadoc on all public and
protected methods. Static-analysis violations reduced from 16 to 4 in the final iteration.

---

## Testing notes

The interesting problems were in making the code testable at all:

**Mocking a static singleton.** `FirebaseManager` exposed only `getInstance()`, so no test
could substitute a fake. We added an `installedFirebaseManager` field that lets a test
install a stub onto the live instance, after which every `getInstance()` returns it —
dependency injection without abandoning the singleton. This is what made US6, US15 and
US18 testable.

**Async callbacks firing after assertions.** `ValueEventListener` callbacks arrived after
the test had already finished asserting. Writing a dedicated `RatingValueEventListener`
let the test drive the callback directly instead of waiting on the network.

**Cross-test state pollution.** Calling `FirebaseManager.install()` directly leaked state
between tests; replaced with a `FirebaseManagerTestInstaller` plus a teardown reset.

**The Firebase emulator never stabilized**, so tests were running against the production
database and leaking state between runs. We stopped touching Firebase in unit tests
entirely and mocked it instead.

---

## Running it

`app/google-services.json` is **not** in this repository — it holds the Firebase project
configuration and API key for the original course project. To run the app you need your
own Firebase project:

1. Create a project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.example.a3130_grp8_app`
3. Enable **Authentication** (Email/Password) and **Realtime Database**
4. Download `google-services.json` into `app/`
5. Build: `./gradlew assembleDebug`

`app/google-services.json.example` shows the expected shape.

Tests: `./gradlew test` (unit) and `./gradlew connectedAndroidTest` (instrumented, needs a
device or emulator).

---

## Results

Final course grade **A**. Demo presentation 100%. Iteration scores 92% → 95% → **96.9%**.
Peer evaluation 5/5 across all six categories, with contribution essentially equal across
the team.

Iteration 2 was our weakest: deadlines were set only for implementation, so merge requests
piled into the final days, and an infrastructure effort ran for weeks without being
re-planned. The retrospective produced three changes — separate deadlines for tests and for
implementation, deadlines agreed by each owner rather than assigned, and an explicit rule
that missing one meant no further work assignment. Iteration 3 was the team's best, with
time left over for UI and code quality.
