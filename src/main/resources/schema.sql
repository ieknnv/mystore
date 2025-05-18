DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS carts;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS "items" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(256) NOT NULL,
	"description" varchar(255) NOT NULL,
	"item_image" bytea NOT NULL,
	"price" numeric(10,2) NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "users" (
	"id" bigserial NOT NULL UNIQUE,
	"name" varchar(256) NOT NULL,
	"email" varchar(256) NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "carts" (
	"id" bigserial NOT NULL UNIQUE,
	"user_id" bigint NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "cart_items" (
	"id" bigserial NOT NULL UNIQUE,
	"cart_id" bigint NOT NULL,
	"item_id" bigint NOT NULL,
	"quantity" bigint NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "orders" (
	"id" bigserial NOT NULL UNIQUE,
	"user_id" bigint NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "order_items" (
	"id" bigserial NOT NULL UNIQUE,
	"order_id" bigint NOT NULL,
	"item_id" bigint NOT NULL,
	"quantity" bigint NOT NULL,
	"price" numeric(10,2) NOT NULL,
	PRIMARY KEY ("id")
);

ALTER TABLE "carts" ADD CONSTRAINT "carts_fk1" FOREIGN KEY ("user_id") REFERENCES "users"("id");
ALTER TABLE "cart_items" ADD CONSTRAINT "cart_items_fk1" FOREIGN KEY ("cart_id") REFERENCES "carts"("id");
ALTER TABLE "cart_items" ADD CONSTRAINT "cart_items_fk2" FOREIGN KEY ("item_id") REFERENCES "items"("id");
ALTER TABLE "orders" ADD CONSTRAINT "orders_fk1" FOREIGN KEY ("user_id") REFERENCES "users"("id");
ALTER TABLE "order_items" ADD CONSTRAINT "order_items_fk1" FOREIGN KEY ("order_id") REFERENCES "orders"("id");
ALTER TABLE "order_items" ADD CONSTRAINT "order_items_fk2" FOREIGN KEY ("item_id") REFERENCES "items"("id");