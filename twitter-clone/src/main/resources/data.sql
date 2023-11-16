INSERT INTO producer (id)
VALUES (0x96b0c311924249288b985a12723cc901),
       (0x22bd0bbd1b1b4a509ee62a1a66b8561b),
       (0x0e8bdc0f912745ba9d298ed8e4f55592);

INSERT INTO subscriber (id)
VALUES (0x96b0c311924249288b985a12723cc901),
       (0x71db7a3da217408a83437ddf925db6ef);

INSERT INTO subscription (id, subscriber_id, producer_id)
VALUES (0xf46c4dc40c234cfaa0d5e1d56c69cea2, 0x96b0c311924249288b985a12723cc901, 0x22bd0bbd1b1b4a509ee62a1a66b8561b),
       (0x41148655effd4d888a163320ff956490, 0x71db7a3da217408a83437ddf925db6ef, 0x96b0c311924249288b985a12723cc901),
       (0x38a2d2cb4b77468889f26aa0979e18b8, 0x71db7a3da217408a83437ddf925db6ef, 0x0e8bdc0f912745ba9d298ed8e4f55592);

INSERT INTO `message` (id, content, producer_id, created)
VALUES
(0x05f6b0127a604e5695e810a6d6540854,
'Man suffers only because he takes seriously what the gods made for fun.',
0x0e8bdc0f912745ba9d298ed8e4f55592,
'2023-09-15 08:23:41'),
(0x9ab2805dc7a44a5cbbf867a9b4d9fdb1,
'It is an ironic habit of human beings to run faster when they have lost their way.',
0x0e8bdc0f912745ba9d298ed8e4f55592,
'2023-08-07 15:27:36'),
(0x175e97f1505948d28e14200f05c23da1,
'Time is a game played beautifully by children.',
0x22bd0bbd1b1b4a509ee62a1a66b8561b,
'2023-09-24 01:22:19'),
(0x36c021ac91d74f9c84ee283604f88b3d,
'Expect everything, I always say, and the unexpected never happens.',
0x96b0c311924249288b985a12723cc901,
'2023-04-30 16:45:30'),
(0x3cdc144220a5400196c368b85d722ad8,
'We are made of star-stuff. We are a way for the universe to know itself.',
0x22bd0bbd1b1b4a509ee62a1a66b8561b,
'2022-10-17 07:09:48'),
(0x313e444d9c3640dfb822f1f7a1324902,
'No! Try not. Do, or do not. There is no try.',
0x96b0c311924249288b985a12723cc901,
'2023-02-05 11:34:22'),
(0x1f7a7be5d3134e7db4868631352cbc96,
'There is no escape—we pay for the violence of our ancestors.',
0x22bd0bbd1b1b4a509ee62a1a66b8561b,
'2022-12-10 09:12:57');