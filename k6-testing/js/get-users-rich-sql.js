import { baseGetTest, defaultOptions } from './base-get-test.js';

export const options = defaultOptions;

export default baseGetTest({
  metricName: 'GetUsersRichSql',
  errorMetricName: 'GetUsersSqlError',
  buildUrl: (baseUrl) => `${baseUrl}/api/users/rich/sql`,
});
