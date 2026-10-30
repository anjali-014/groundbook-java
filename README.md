# GroundBook

Four grounds and one booking book: every slot with the buffers around it, so no two clubs ever meet at the gate.

## Run it in a container

You need Docker and nothing else: the image builds the app itself. From this folder:

    docker build -t groundbook .
    docker run -p 8080:8080 groundbook

Open http://localhost:8080/ for the day strip - or http://<the office PC's IP>:8080/ from Sabu's gate tablet.
`http://localhost:8080/actuator/health` answers `{"status":"UP"}` while the app is up and can reach its database.

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
- [ ] Ticket 10 · It survives being switched off

Built by Anjali Saini on [LearnTube Guided Projects](https://guidedprojects.learntube.ai).
<!-- learntube:end -->
