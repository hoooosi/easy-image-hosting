INSERT INTO "tb_user" ("id", "account", "password", "name", "avatar", "profile", "role", "create_time", "update_time")
VALUES (1973285583948988417, 'testaccount', '6d8d5ef890feb26829ce18b1406313ca', 'testuser', NULL, NULL, 'USER',
        '2025-10-01 15:15:19.744814', NULL);


INSERT INTO "tb_space" ("id", "name", "max_size", "total_size", "public_permission_mask", "member_permission_mask",
                        "create_time", "update_time")
VALUES (10001, 'test_space_001', 1073741824, 0, 15, 2097167, '2025-10-01 15:16:55.725451', NULL),
       (10002, 'test_space_002', 1073741824, 0, 3, 2097167, '2025-10-01 15:16:55.749451', NULL);

INSERT INTO "tb_member" ("id", "sid", "uid", "permission_mask", "create_time", "update_time") VALUES
                                                                                                  (1973285986560159746,	10001,	1973285583948988417,	9223372036854775807,	'2025-10-01 15:16:55.733451',	NULL),
                                                                                                  (1973285986631462913,	10002,	1973285583948988417,	9223372036854775807,	'2025-10-01 15:16:55.750457',	NULL);