INSERT INTO `role` (id, `name`, `description`) VALUES
  (0x5b846fc56fff4d3f9f4db86b1b94e5e8, 'ADMIN', 'Administrator'),
  (0xeb5f5e1690b048b7bc9ec2b40291aac6, 'SUBSCRIBER', 'A subscriber to other messages'),
  (0xb87c6bdcf14347788dd9ec047a0c1903, 'PRODUCER', 'A producer that distributes messages');

INSERT INTO user (id, username, created) VALUES
  (0x96b0c311924249288b985a12723cc901, 'pandarie', CURRENT_TIMESTAMP()),
  (0x22bd0bbd1b1b4a509ee62a1a66b8561b, 'everhazako', CURRENT_TIMESTAMP()),
  (0x71db7a3da217408a83437ddf925db6ef, 'ashbelick', CURRENT_TIMESTAMP()),
  (0x0e8bdc0f912745ba9d298ed8e4f55592, 'capponie', CURRENT_TIMESTAMP());

INSERT INTO `session` (id, user_id, login_at, expire_at) VALUES
  (0xacc68f9f36b0450c90c21bac004d37ed, 0x96b0c311924249288b985a12723cc901, '2023-06-12 09:00:00', '2023-06-15 17:00:00'),
  (0xf9450e61009a408b9b1f9218056d19fd, 0x71db7a3da217408a83437ddf925db6ef, '2023-11-11 10:00:00', '2023-11-20 18:00:00'),
  (0x6ea5ae39503b41f19294b8909d1c84b3, 0x0e8bdc0f912745ba9d298ed8e4f55592, '2023-08-01 10:00:00', '2023-09-12 18:00:00'),
  (0xc99baa7eea1e42baac179f57f27c34d9, 0x22bd0bbd1b1b4a509ee62a1a66b8561b, '2023-09-07 10:00:00', '2023-11-09 18:00:00');

INSERT INTO user_role (user_id, role_id) VALUES
  (0x96b0c311924249288b985a12723cc901, 0xeb5f5e1690b048b7bc9ec2b40291aac6),
  (0x96b0c311924249288b985a12723cc901, 0xb87c6bdcf14347788dd9ec047a0c1903),
  (0x22bd0bbd1b1b4a509ee62a1a66b8561b, 0x5b846fc56fff4d3f9f4db86b1b94e5e8),
  (0x22bd0bbd1b1b4a509ee62a1a66b8561b, 0xb87c6bdcf14347788dd9ec047a0c1903),
  (0x71db7a3da217408a83437ddf925db6ef, 0xeb5f5e1690b048b7bc9ec2b40291aac6),
  (0x0e8bdc0f912745ba9d298ed8e4f55592, 0xb87c6bdcf14347788dd9ec047a0c1903);
