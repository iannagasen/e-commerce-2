
URLS:
  - EurekaDiscoveryServer - http://localhost:8761

ERRORS:
  `MessageDeliveryException ... doesn't have subscribers to accept message`
    - Error during runtime/processing
    - usually runtime exception
    - or misconfiguration

- If you supply a Mono using `.map()` it will not trigger that Mono
  - always use `.flatMap()` when trying to sequencially trigger another Mono