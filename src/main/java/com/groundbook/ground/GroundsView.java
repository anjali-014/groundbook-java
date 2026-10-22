package com.groundbook.ground;

import java.util.List;

/** GET /venues/{venue}/grounds. */
public record GroundsView(String venue, List<GroundView> grounds) {
}