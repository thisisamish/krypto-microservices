-- ---------------------------------------------
-- Carts (one cart per user because user_id is UNIQUE)
-- ---------------------------------------------
INSERT INTO carts (id, user_id, created_at, updated_at)
VALUES
  (1, 7, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),  -- talhakhan
  (2, 8, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());  -- govindsingh

INSERT INTO cart_items (id, cart_id, product_id, product_name, quantity, unit_price, created_at, updated_at)
VALUES
  (1, 1, 3, 'Harvest Gold - 100% Atta Bread', 2, 46.75, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
  (2, 1, 1, 'Amul Pasteurised Butter',        1, 55.80, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
  (3, 2, 4, 'Pansari Kacchi Ghani Mustard Oil', 1, 210.00, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- ---------------------------------------------
-- Orders
-- order_number <= 32 chars, unique
-- status, payment_status, payment_method are string enums
-- Address is @Embedded on orders; columns use the Address field names
-- ---------------------------------------------

-- Order #1 : amishverma buys 2×Butter + 1×Bread
-- Subtotal = 2*55.80 + 1*46.75 = 158.35
-- Grand total = 158.35 + 0.00 tax + 15.00 shipping - 0.00 discount = 173.35
INSERT INTO orders
  (id, order_number, user_id, status, payment_status, payment_method,
   payment_reference, paid_at, username_snapshot,
   subtotal, tax, shipping_fee, discount, grand_total,
   full_name, line1, line2, city, state, postal_code, country, phone,
   notes, created_at, updated_at)
VALUES
  (1, 'ORD-20250214-0001', 5, 'PAID', 'SUCCESS', 'MOCK',
   'PAYMOCK-001', CURRENT_TIMESTAMP(), 'amishverma',
   158.35, 0.00, 15.00, 0.00, 173.35,
   'Amish Verma', '221B Baker St', '', 'Delhi', 'DL', '110001', 'IN', '9999999999',
   'Leave at the door', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO order_items
  (id, order_id, product_id, product_name, unit_price, quantity, line_total)
VALUES
  (1, 1, 1, 'Amul Pasteurised Butter', 55.80, 2, 111.60),
  (2, 1, 3, 'Harvest Gold - 100% Atta Bread', 46.75, 1, 46.75);

-- Order #2 : akanksha buys 1×Salt + 1×Mustard Oil
-- Subtotal = 28.50 + 210.00 = 238.50; Grand total = 253.50
INSERT INTO orders
  (id, order_number, user_id, status, payment_status, payment_method,
   payment_reference, paid_at, username_snapshot,
   subtotal, tax, shipping_fee, discount, grand_total,
   full_name, line1, line2, city, state, postal_code, country, phone,
   notes, created_at, updated_at)
VALUES
  (2, 'ORD-20250214-0002', 6, 'SHIPPED', 'SUCCESS', 'COD',
   'COD-987654', CURRENT_TIMESTAMP(), 'akanksha',
   238.50, 0.00, 15.00, 0.00, 253.50,
   'Akanksha', 'Plot 10, Main Road', '', 'Mumbai', 'MH', '400001', 'IN', '9888888888',
   NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO order_items
  (id, order_id, product_id, product_name, unit_price, quantity, line_total)
VALUES
  (3, 2, 2, 'Tata Salt, Iodised Namak', 28.50, 1, 28.50),
  (4, 2, 4, 'Pansari Kacchi Ghani Mustard Oil', 210.00, 1, 210.00);
