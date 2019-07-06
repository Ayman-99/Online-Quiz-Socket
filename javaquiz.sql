
CREATE TABLE `users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `score` int(11) NOT NULL
) ;

ALTER TABLE `users`
  ADD UNIQUE KEY `username` (`username`);
COMMIT;
