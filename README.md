# GroundBook

Four grounds and one booking book: every slot with the buffers around it, so no two clubs ever meet at the gate.

## Run it in a container

You need Docker and nothing else: the image builds the app itself (in a first stage, on a JDK) and runs it on a JRE.
From this folder:
    docker build -t groundbook .
    docker run -p 8080:8080 groundbook

Open http://localhost:8080/ for the day strip - or http://<the office PC's IP>:8080/ from Sabu's gate tablet. That
run keeps its database inside the container and loses it with the container.

## Keep the bookings, and configure it from one file

Every setting can come from the environment; the table below lists the ones you may need to change. Copy
`.env.example` to `.env`, change what you need, and run with the database on a named volume at `/data`:

    cp .env.example .env
    docker run -d --name groundbook -p 8080:8080 -v groundbook-data:/data --env-file .env groundbook

    | Variable | What it sets | Default |
|---|---|---|
| `DATABASE_URL` | the JDBC URL of the H2 file | `jdbc:h2:file:./data/groundbook` (`jdbc:h2:file:/data/groundbook` in `.env.example`) |
| `HOLD_HOURS` | how long a requested slot holds the ground before it must be confirmed | `24` |
| `HOST` | the address the server listens on | `127.0.0.1` (`0.0.0.0` in the image) |
| `PORT` | the port | `8080` |

The buffers are not a setting: each event type's setup and teardown minutes come from `seed/event_types.csv`.

To hold requests for 48 hours: change the line in `.env`, then `docker rm -f groundbook` and run the same
`docker run` again. Nothing is rebuilt, and Saturday's bookings are all still there, because they live on the volume.

Logs go to stdout: `docker logs groundbook` shows one line per request and one per event - never a club's name.


## One booking, from the request to the gate

With the container running on port 8080 (use a coming Saturday for the slot, and the same date in the day-strip call):

    curl -s http://localhost:8080/actuator/health
    # {"status":"UP"}

    curl -s -X POST http://localhost:8080/bookings -H 'content-type: application/json' -d '{"ground":"A","event":"league","from":"2026-10-03T14:00:00Z","to":"2026-10-03T17:00:00Z","club":"Ambedkar Nagar CC"}'
    # {"bookingId":"BK0001","ground":"A","event":"league",...,"groundHeldFrom":"2026-10-03T13:20:00Z","groundHeldTo":"2026-10-03T17:30:00Z","state":"requested",...}

    curl -s -X POST http://localhost:8080/bookings/BK0001/confirm
    # {"bookingId":"BK0001","state":"confirmed","confirmedAt":"..."}

    curl -s "http://localhost:8080/venues/deccan/grounds/A/day?date=2026-10-03"
    # the day strip: the block 14:00-17:00 and its shadow 13:20-17:30

    curl -s http://localhost:8080/bookings/BK0001
    # what Sabu checks before he unlocks the gate: confirmed, ground A, 14:00-17:00, and its history

Use the id your own request returned. The API docs are at http://localhost:8080/swagger-ui.html.

<!-- learntube:start -->
---

[![tests](https://github.com/anjali-014/groundbook-java/actions/workflows/tests.yml/badge.svg)](https://github.com/anjali-014/groundbook-java/actions/workflows/tests.yml)

### Progress

- [x] Ticket 1 · The complex, as a day you can look at
- [x] Ticket 2 · A club asks for a slot
- [x] Ticket 3 · Confirm, cancel, and the hour that has already gone
- [x] Ticket 4 · The refusals, once the shadows are counted
- [x] Ticket 5 · Moving a booking without breaking the day
- [x] Ticket 6 · A planner nobody can erase
- [x] Ticket 7 · One file becomes an app
- [x] Ticket 8 · The rule table, and then the rule changes
- [x] Ticket 9 · It runs on the office machine
- [x] Ticket 10 · It survives being switched off

Built by Anjali Saini on [LearnTube Guided Projects](https://guidedprojects.learntube.ai).
<!-- learntube:end -->
