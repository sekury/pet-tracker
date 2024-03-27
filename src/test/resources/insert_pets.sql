--- CATS
INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('CAT', 1, true);
INSERT INTO CAT (id, tracker_type, lost_tracker) VALUES ((SELECT MAX(id) FROM PET),'L', false);

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('CAT', 2, false);
INSERT INTO CAT (id, tracker_type, lost_tracker) VALUES ((SELECT MAX(id) FROM PET),'L', false);

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('CAT', 3, true);
INSERT INTO CAT (id, tracker_type, lost_tracker) VALUES ((SELECT MAX(id) FROM PET),'S', true);

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('CAT', 4, false);
INSERT INTO CAT (id, tracker_type, lost_tracker) VALUES ((SELECT MAX(id) FROM PET),'S', true);

--- DOGS
INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('DOG', 1, true);
INSERT INTO DOG (id, tracker_type) VALUES ((SELECT MAX(id) FROM PET),'L');

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('DOG', 2, false);
INSERT INTO DOG (id, tracker_type) VALUES ((SELECT MAX(id) FROM PET),'L');

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('DOG', 3, true);
INSERT INTO DOG (id, tracker_type) VALUES ((SELECT MAX(id) FROM PET),'M');

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('DOG', 4, false);
INSERT INTO DOG (id, tracker_type) VALUES ((SELECT MAX(id) FROM PET),'M');

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('DOG', 5, true);
INSERT INTO DOG (id, tracker_type) VALUES ((SELECT MAX(id) FROM PET),'S');

INSERT INTO PET (pet_type, owner_id, in_zone) VALUES ('DOG', 6, false);
INSERT INTO DOG (id, tracker_type) VALUES ((SELECT MAX(id) FROM PET),'S');