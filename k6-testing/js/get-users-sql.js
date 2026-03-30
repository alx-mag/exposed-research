import { baseGetTest, defaultOptions } from './base-get-test.js';

export const options = defaultOptions;

export default baseGetTest({
  metricName: 'GetUsersSql',
  errorMetricName: 'GetUsersSqlError',
  buildUrl: (baseUrl) => `${baseUrl}/api/users/sql`,
});
