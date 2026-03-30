import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

export const defaultOptions = {
  stages: [
    { duration: '10s', target: `${__ENV.USERS}` },
    { duration: '100s', target: `${__ENV.USERS}` },
    { duration: '10s', target: 0 },
  ],
};

function logIfUnexpectedStatus(name, response, expectedStatus) {
  if (response.status === expectedStatus) {
    return;
  }

  console.error(
    `[${name}] status=${response.status} method=${response.request.method} url=${response.request.url} body=${JSON.stringify(response.body)}`
  );
}

export function baseGetTest({
  metricName,
  errorMetricName,
  requestName = 'Get Users',
  expectedStatus = 200,
  buildUrl,
}) {
  const trend = new Trend(metricName);
  const errorRate = new Rate(errorMetricName);

  return function () {
    const sleepMs = Number(__ENV.SLEEP_MS ?? '0');
    const baseUrl = `${__ENV.BASE_URL}`;
    const params = {
      headers: {
        'Content-Type': 'application/json',
      },
    };
    const requestUrl = buildUrl(baseUrl);
    const requests = {
      [requestName]: {
        method: 'GET',
        url: requestUrl,
        params,
      },
    };

    const responses = http.batch(requests);
    const response = responses[requestName];

    check(response, {
      [`status is ${expectedStatus}`]: (r) => r.status === expectedStatus,
    }) || errorRate.add(1);
    logIfUnexpectedStatus(requestName, response, expectedStatus);

    trend.add(response.timings.duration);

    if (sleepMs > 0) {
      sleep(sleepMs / 1000);
    }
  };
}
