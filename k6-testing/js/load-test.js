import http from 'k6/http';
import { check } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const getTrend = new Trend('GetUsers');
const getErrorRate = new Rate('GetUsersError');

const postTrend = new Trend('AddBook');
const postErrorRate = new Rate('AddBookError');

const createUserTrend = new Trend('AddUser');
const createUserErrorRate = new Rate('AddUserError');

const orderTrend = new Trend('AddOrder');
const orderErrorRate = new Rate('AddOrderError');

function logIfUnexpectedStatus(name, response, expectedStatus) {
  if (response.status === expectedStatus) {
    return;
  }
  console.error(
    `[${name}] status=${response.status} method=${response.request.method} url=${response.request.url} body=${JSON.stringify(response.body)}`
  );
}

export let options = {
  stages: [
      { duration: "10s", target: `${__ENV.USERS}` },
      { duration: "100s", target: `${__ENV.USERS}` },
      { duration: "10s", target: 0 }
  ]
};

export default function () {
  const baseUrl = `${__ENV.BASE_URL}`;

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const addUserBody = JSON.stringify({
      name: `User ${__VU}-${__ITER}`,
      email: `user-${__VU}-${__ITER}@example.com`,
      age: 20 + (__ITER % 50),
  });

  const addBookBody = JSON.stringify({
      author: `Author Name ${__ITER}`,
      isbn: `${__VU}-${__ITER}`,
      title: `Book ${__VU}-${__ITER}`,
      year: 1900 + (__ITER % 100)
  });

  const requests = {
      'Get Users': {
        method: 'GET',
        url: `${baseUrl}/api/users`,
        params: params,
      },
      'Add User': {
        method: 'POST',
        url: `${baseUrl}/api/users`,
        params: params,
        body: addUserBody,
      },
      'Add Book': {
        method: 'POST',
        url: `${baseUrl}/api/books`,
        params: params,
        body: addBookBody,
      }
    };

  const responses = http.batch(requests);
  const getResp = responses['Get Users'];
  const createUserResp = responses['Add User'];
  const postResp = responses['Add Book'];

  check(getResp, {
    'status is 200': (r) => r.status === 200,
  }) || getErrorRate.add(1);
  logIfUnexpectedStatus('Get Users', getResp, 200);

  getTrend.add(getResp.timings.duration);

  check(postResp, {
    'status is 201': (r) => r.status === 201,
  }) || postErrorRate.add(1);
  logIfUnexpectedStatus('Add Book', postResp, 201);

  postTrend.add(postResp.timings.duration);

  check(createUserResp, {
    'status is 201': (r) => r.status === 201,
  }) || createUserErrorRate.add(1);
  logIfUnexpectedStatus('Add User', createUserResp, 201);

  createUserTrend.add(createUserResp.timings.duration);

  if (createUserResp.status !== 201 || postResp.status !== 201) {
    orderErrorRate.add(1);
    return;
  }

  const createdUser = createUserResp.json();
  const createdBook = postResp.json();
  const addOrderBody = JSON.stringify({
      userId: createdUser.id,
      bookId: createdBook.id,
      quantity: (__ITER % 5) + 1,
  });
  const addOrderResp = http.post(`${baseUrl}/api/orders`, addOrderBody, params);

  check(addOrderResp, {
    'status is 201': (r) => r.status === 201,
  }) || orderErrorRate.add(1);
  logIfUnexpectedStatus('Add Order', addOrderResp, 201);

  orderTrend.add(addOrderResp.timings.duration);
}
