package com.groupeight.order_service.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders", indexes = { @Index(name = "idx_order_user_created_at", columnList = "user_id, created_at"),
		@Index(name = "idx_order_number", columnList = "order_number", unique = true) })
public class Order implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_number", nullable = false, unique = true, length = 32)
	private String orderNumber;

	@Column(name = "user_id")
	private Long userId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private OrderStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 16)
	private PaymentStatus paymentStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 16)
	private PaymentMethod paymentMethod;

	@Column(name = "payment_reference")
	private String paymentReference;

	@Column(name = "paid_at")
	private Instant paidAt;

	@Column(name = "username_snapshot")
	private String usernameSnapshot;

	@Builder.Default
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	@Column(precision = 12, scale = 2, nullable = false)
	private BigDecimal subtotal;

	@Column(precision = 12, scale = 2, nullable = false)
	private BigDecimal tax;

	@Column(name = "shipping_fee", precision = 12, scale = 2, nullable = false)
	private BigDecimal shippingFee;

	@Column(precision = 12, scale = 2, nullable = false)
	private BigDecimal discount;

	@Column(name = "grand_total", precision = 12, scale = 2, nullable = false)
	private BigDecimal grandTotal;

	@Embedded
	private Address shippingAddress;

	@Column(length = 512)
	private String notes;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Instant updatedAt;

	public void addItem(OrderItem item) {
		item.setOrder(this);
		this.items.add(item);
	}
}
