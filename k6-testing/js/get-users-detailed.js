import { baseGetTest, defaultOptions } from './base-get-test.js';

export const options = defaultOptions;

export default baseGetTest({
  metricName: 'GetUsers',
  errorMetricName: 'GetUsersError',
  buildUrl: (baseUrl) => `${baseUrl}/api/users/rich`,
});
