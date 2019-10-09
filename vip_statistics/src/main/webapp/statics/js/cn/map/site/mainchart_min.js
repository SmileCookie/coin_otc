!
function() {
	var t, n, e, r, i, o, u, l, a, s, c, f, h, d, p, g, m, v, y, _, x, w, b, k, T, M, C, S, F, A, B, D, P, I, R, O, N, E, L, W, U, G, H, q, z, Y, j, J, X, K, Q, V, Z, tn, nn, en, rn, on, un, ln, an, sn, cn, fn, hn, dn, pn, gn, periodOptions, vn, yn, _n, xn, $n, wn, bn, kn, Tn, Mn, Cn, Sn, Fn, An, Bn, Dn, Pn, In, Rn, On, Nn, En, Ln, updateLastBarTimePassed, Un, Gn, Hn, qn, zn, Yn, jn, Jn, Xn, Kn, Qn, Vn, Zn, te, ne, ee, re, ie, oe, ue, le, ae, historicalData, ce, fe, he, de, pe, ge, me = {}.hasOwnProperty,
	resizeBlocks, ve = [].slice; !
	function() {
		var t, n, e, r, i;
		$(function() {
			function o() {
				var n, e, r, i, o;
				return i = $(this),
				i.addClass("dropdown-hover"),
				n = $(".dropdown-data", this),
				o = .5 * (i.outerWidth() - n.outerWidth()),
				r = i.offset().left + i.outerWidth() - $(window).width(),
				r > o && (o = r),
				e = i.offset().left + i.outerWidth() - n.outerWidth(),
				o > e && (o = e),
				n.css("right", o),
				t = this
			}
			function u() {
				var n;
				return $(this).removeClass("dropdown-hover"),
				n = !1,
				t = null
			}
			function l() {
				var e = this;
				return n = !0,
				t ? (u.call(t), o.call(this), void 0) : this.showing ? void 0 : (this.showing = !0, setTimeout(function() {
					return n && (t && u.call(t), o.call(e)),
					e.showing = !1
				},
				80))
			}
			function a() {
				var t = this;
				return n = !1,
				this.hiding ? void 0 : (this.hiding = !0, setTimeout(function() {
					return n || u.call(t),
					t.hiding = !1
				},
				80))
			}
			var s, c, f;
			for (n = !1, t = null, f = $(".dropdown1"), s = 0, c = f.length; c > s; s++) e = f[s],
			r = function() {
				var t = this;
				return $(".t", this).click(function() {
					return $(".dropdown-data", t).is(":visible") ? u.call(t) : o.call(t)
				})
			},
			r.call(e);
			return window.$is_mobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent),
			$is_mobile ? void 0 : (i = !1, $(".dropdown1").hover(function() {
				return l.call(this)
			},
			function() {
				return a.call(this)
			}))
		})
	} (),
	function() {
		var t, n, e;
		$(function() {
			var r;
			r = function(t, n) {
				var r, i, o, u, l, a, s, c, f;
				return o = $("#setting_" + n.id),
				s = null != (c = null != (f = $.cookie(n.id)) ? f.toLowerCase() : void 0) ? c: "",
				i = !1,
				r = null,
				a = function() {
					var t, a;
					t = n.options,
					a = [];
					for (u in t) me.call(t, u) && (l = t[u], a.push(function(t, u) {
						var l;
						return l = $("li[value=" + u + "]", o),
						l.active = function() {
							return l.addClass("active"),
							n.value = u
						},
						l.click(function() {
							return $("li", o).removeClass("active"),
							l.active(),
							$.cookie(n.id, u, {
								expires: 3650,
								path: "/"
							}),
							n.refresh ? window.location.reload() : (e(), world_draw_main())
						}),
						u === s && (i = !0, l.active()),
						u === n["default"] && (r = l),
						l
					} (u, l)));
					return a
				} (),
				i ? void 0 : n["default"] && r ? r.active() : a[0].active()
			};
			for (t in $settings) me.call($settings, t) && (n = $settings[t], r(t, n));
			return (e = function() {
				var t;
				return "line" === (t = $settings.stick_style.value) || "line_o" === t ? $(".line_style").show() : $(".line_style").hide()
			})(),
			null
		})
	} (),
	ne = on = ue = rn = Zn = R = en = oe = Yn = s = On = te = r = c = B = Dn = Pn = In = Jn = null,
	function() {
		var t, n;
		return rn = function(t, n) {
			var e, r, i;
			return i = !1,
			r = t,
			e = function() {
				function e() {
					r > 0 ? ue(16,
					function() {
						e(r -= 16)
					}) : o()
				}
				function o() {
					return n(),
					i = !1
				}
				return r = t,
				i ? !0 : (i = !0, e(), void 0)
			}
		},
		rn.statuses = {},
		ne = function() {
			return console.log.apply(console, arguments)
		},
		on = function() {
			return console.log.apply(console, [new Date].concat(ve.call(arguments)))
		},
		ue = function(t, n) {
			return setTimeout(n, t)
		},
		R = function() {
			var t, n, e, r, i, o, u, l;
			for (e = arguments[0], r = 3 <= arguments.length ? ve.call(arguments, 1, i = arguments.length - 1) : (i = 1, []), n = arguments[i++], l = [], o = 0, u = n.length; u > o; o++) t = n[o],
			"object" == typeof t && t.length ? l.push(e.apply(null, ve.call(r).concat(ve.call(t)))) : l.push(e.apply(null, ve.call(r).concat([t])));
			return l
		},
		oe = function(t, n) {
			return null == n && (n = "world"),
			("undefined" != typeof console && null !== console ? console.time: void 0) ? (console.time(n), t(), console.timeEnd(n)) : t()
		},
		Yn = function(t) {
			return t[t.length - 1]
		},
		en = function(t) {
			var n, e, r;
			if (t.length) return t.slice(0);
			e = {};
			for (n in t) me.call(t, n) && (r = t[n], e[n] = r);
			return e
		},
		t = 0,
		s = function(n) {
			var e, r, i;
			for (i = [], e = r = 0; n >= 0 ? n > r: r > n; e = n >= 0 ? ++r: --r) i.push(t++);
			return i
		},
		On = function() {
			var t, n, e, r, i, o;
			return t = 1 <= arguments.length ? ve.call(arguments, 0) : [],
			n = t.pop(),
			i = t[0],
			e = t[1],
			null == e && (e = {}),
			"?" !== i[i.length - 1],
			"undefined" != typeof XDomainRequest && null !== XDomainRequest ? (i = -1 === i.indexOf("?") ? i + "?" + $.param(e) : i + "&" + $.param(e), o = new XDomainRequest, o.open("GET", i), o.onload = function() {
				var resjsons = $.parseJSON(o.responseText);
				resjsons.sort(function (a, b) {
					　return b[0]- a[0];
				}); 
				return e = resjsons,
				e ? n(null, e) : n(new Error("parse json failed"), null)
			},
			o.onerror = function() {
				return n("error", null)
			},
			o.ontimeout = function() {},
			o.onprogress = function() {},
			o.timeout = 6e4, o.send(), o) : (r = $.ajax({
				url: i,
				type: "GET",
				dataType: "json",
				timeout: 6e4,
				data: e
			}), r.done(function(t) {
				return n(null, t)
			}), r.fail(function(t, e, r) {
				var i;
				return "error" === e && (e = ""),
				i = r || e || "",
				n(new Error(i), null)
			}))
		},
		te = function(t) {
			var n;
			return n = function() {
				function n(n) {
					return n ? t(i) : r.apply(null, e)
				}
				var e, r, i, o, u;
				o = arguments[0],
				e = 3 <= arguments.length ? ve.call(arguments, 1, u = arguments.length - 1) : (u = 1, []),
				r = arguments[u++],
				o.apply(null, ve.call(e).concat([function() {
					n((i = arguments[0], e = 2 <= arguments.length ? ve.call(arguments, 1) : [], i))
				}]))
			}
		},
		r = function() {
			function t() {
				this.push_cbs = [],
				this.args = [],
				this.shift_cbs = []
			}
			var n;
			return t.prototype.push = function() {
				var t, n, e, r;
				return t = 2 <= arguments.length ? ve.call(arguments, 0, r = arguments.length - 1) : (r = 0, []),
				n = arguments[r++],
				(e = this.shift_cbs.shift()) ? this.process(t, e, n) : (this.push_cbs.push(n), this.args.push(t))
			},
			t.prototype.unshift = function() {
				var t, n, e, r;
				return t = 2 <= arguments.length ? ve.call(arguments, 0, r = arguments.length - 1) : (r = 0, []),
				n = arguments[r++],
				(e = this.shift_cbs.shift()) ? this.process(t, e, n) : (this.push_cbs.unshift(n), this.args.unshift(t))
			},
			t.prototype.shift = function(t) {
				var n, e;
				return (e = this.push_cbs.shift()) ? (n = this.args.shift(), this.process(n, t, e)) : this.shift_cbs.push(t)
			},
			n = 0,
			t.prototype.process = function(t, e, r) {
				function i() {
					return r(),
					e.apply(null, t)
				}
				100 === ++n ? (n = 0, ue(0,
				function() {
					i()
				})) : i()
			},
			t
		} (),
		n = {},
		Dn = function() {
			var t, e, r, i, o;
			return i = arguments[0],
			t = 3 <= arguments.length ? ve.call(arguments, 1, o = arguments.length - 1) : (o = 1, []),
			e = arguments[o++],
			(r = n[i]) ? r.channel.push(t, e) : void 0
		},
		Pn = function() {
			var t, e, r, i, o;
			return i = arguments[0],
			t = 3 <= arguments.length ? ve.call(arguments, 1, o = arguments.length - 1) : (o = 1, []),
			e = arguments[o++],
			(r = n[i]) ? r.channel.unshift(t, e) : void 0
		},
		In = function(t, e) {
			var i;
			return (i = n[t]) ? i.actions.push(e) : (i = n[t] = {
				actions: [e],
				channel: new r,
				running: !1
			},
			function() {
				function t() {
					i.channel.shift(function() {
						function o() {
							a++,
							u()
						}
						function u() {
							s > a ? (e = c[a], e.apply(null, ve.call(n).concat([function(t) {
								o(t)
							}]))) : l()
						}
						function l() {
							t(r)
						}
						var a, s, c;
						n = arguments[0],
						c = i.actions,
						a = 0,
						s = c.length,
						u()
					})
				}
				var n, r = this;
				t()
			} ())
		},
		Jn = function() {
			function t() {
				r(i,
				function() {
					return e()
				})
			}
			var n, e, r, i, o, u;
			return n = 3 <= arguments.length ? ve.call(arguments, 0, u = arguments.length - 2) : (u = 0, []),
			r = arguments[u++],
			e = arguments[u++],
			o = 2e3,
			i = te(function(n) {
				Dn("loop_until_success:error", n, o,
				function() {
					ue(o,
					function() {
						return o += 2e3,
						o > 2e4 && (o = 2e4),
						t()
					})
				})
			}),
			t()
		},
		c = Zn,
		B = On,
		ne = ne,
		on = on,
		ue = ue,
		rn = rn,
		Zn = Zn,
		R = R,
		en = en,
		oe = oe,
		Yn = Yn,
		s = s,
		On = On,
		te = te,
		r = r,
		c = c,
		B = B,
		Dn = Dn,
		Pn = Pn,
		In = In,
		Jn = Jn,
		Zn = function() {
			var t, n, e, r, i, o;
			if (1 === arguments.length) Zn("", arguments[0]);
			else {
				if (i = arguments[0], e = arguments[1], o = "undefined" != typeof window && null !== window ? window: global) for (r in e) me.call(e, r) && (n = e[r], o[i + r] = n);
				if (t = "undefined" != typeof module && null !== module ? module.exports: void 0) for (r in e) me.call(e, r) && (n = e[r], t[r.replace(/^_/, "")] = n)
			}
			return this
		}
	} (),
	e = t = n = null,
	X = tn = z = K = Q = nn = O = N = L = W = G = E = U = H = j = V = Y = q = J = Z = null,
	function() {
		function r(t) {
			var n, e, r, i, o;
			for (null == t && (t = []), e = {},
			r = [], e[I] = [], i = 0, o = t.length; o > i; i++) n = t[i],
			e[I][n] = [];
			return e[D] = [],
			e[P] = [],
			e
		}
		function i(t) {
			var n, e;
			return e = t[I],
			n = e.length,
			e[n] = [],
			[n, e[n]]
		}
		function o(t, n, e) {
			var r;
			return (null != (r = t[D])[n] ? (r = t[D])[n] : r[n] = []).push(e),
			e
		}
		function u(t, n, e) {
			return o(t, n, e),
			t[P][e](0),
			e
		}
		function l(t, n, e) {
			var r, i, o;
			return o = function() {
				var t;
				t = [];
				for (r in e) me.call(e, r) && (i = e[r], t.push([r, i]));
				return t
			} (),
			un(t, n, o)
		}
		function a(t, n, e) {
			var r, i, o;
			return o = function() {
				var t;
				t = [];
				for (r in e) me.call(e, r) && (i = e[r], t.push([r, i]));
				return t
			} (),
			sn(t, n, o)
		}
		function c(t) {
			var n;
			return n = function() {
				var n, e, r, i, o, u, l, a;
				if (arguments[2].length ? (r = arguments[0], a = arguments[1], l = arguments[2], i = arguments[3]) : (r = arguments[0], a = arguments[1], n = arguments[2], u = arguments[3], i = arguments[4], l = [[n, u]]), e = function() {
					var e, i, o, s;
					for (s = [], e = 0, i = l.length; i > e; e++) o = l[e],
					n = o[0],
					u = o[1],
					t(r, a, n, u),
					s.push(n);
					return s
				} (), o = {},
				null == i && (i = !0), i) for (n in e) ! o[n] && i && r[D][n] && a >= 0 && f(r, r[D][n], a),
				o[n] = !0;
				return e
			}
		}
		function f(t, n, e) {
			var r, i, o, u;
			if (i = t[P], "number" == typeof n) i[n].call(this, e);
			else for (o = 0, u = n.length; u > o; o++) r = n[o],
			f(t, r, e);
			return this
		}
		function h(t, n) {
			var e, r, i, o, u;
			if (r = t[I], "number" == typeof n) return r[n];
			for (u = [], i = 0, o = n.length; o > i; i++) e = n[i],
			r[e] || (r[e] = []),
			u.push(r[e]);
			return u
		}
		function d(t, n, e) {
			var r, i, o, u, l, a;
			for (r = Y(t, e), u = {},
			o = l = 0, a = e.length; a > l; o = ++l) i = e[o],
			u[e[o]] = r[o][n];
			return u
		}
		function p(t, n) {
			var e, r, o, u, l;
			return u = t[I],
			o = t[P],
			l = i(t),
			e = l[0],
			r = l[1],
			o[e] = function(e) {
				return g(t, e,
				function(t) {
					return r[t] = n(t)
				})
			},
			e
		}
		function g(t, n, e) {
			var r, i, o, u;
			for (i = t[I], r = o = n, u = i[0].length; u >= n ? u > o: o > u; r = u >= n ? ++o: --o) e(r);
			return null
		}
		function m(t, n, e) {
			var r, o, u, l, a, s;
			return l = t[I],
			u = t[P],
			s = i(t),
			r = s[0],
			o = s[1],
			a = l[n],
			u[r] = function(t) {
				var n, r, i, u, l, s, c, f;
				for (r = a.length, i = a.slice(t - e, t), u = 0, s = 0, f = i.length; f > s; s++) l = i[s],
				u += l;
				for (n = c = t; r >= t ? r > c: c > r; n = r >= t ? ++c: --c) l = a[n],
				i.length >= e && (u -= i.shift()),
				u += l,
				i.push(l),
				o[n] = u / i.length;
				return this
			},
			r
		}
		function v(t, n, e) {
			var r, o, u, l, a, s;
			return l = t[I],
			u = t[P],
			s = i(t),
			r = s[0],
			o = s[1],
			a = l[n],
			u[r] = function(n) {
				return g(t, n,
				function(t) {
					var n, r, i;
					return n = a[t],
					r = null != (i = o[t - 1]) ? i: n,
					r = (2 * n + (e - 1) * r) / (e + 1),
					o[t] = r
				})
			},
			r
		}
		function y(t, n, e, r) {
			var o, u, l, a, s, c;
			return a = t[I],
			l = t[P],
			c = i(t),
			o = c[0],
			u = c[1],
			s = a[n],
			l[o] = function(n) {
				return g(t, n,
				function(t) {
					var n, i, o;
					return n = s[t],
					i = null != (o = u[t - 1]) ? o: n,
					i = (r * n + (e - r) * i) / e,
					u[t] = i
				})
			},
			o
		}
		function _(t, n, e) {
			var r, o, u, l, a;
			return l = t[I],
			u = t[P],
			a = i(t),
			r = a[0],
			o = a[1],
			u[r] = function(r) {
				return g(t, r,
				function(t) {
					var r, i;
					return i = Math.max(t - e, 0),
					r = t + 1,
					o[t] = Math.min.apply(Math, l[n].slice(i, r))
				})
			},
			r
		}
		function x(t, n, e) {
			var r, o, u, l, a;
			return l = t[I],
			u = t[P],
			a = i(t),
			r = a[0],
			o = a[1],
			u[r] = function(r) {
				return g(t, r,
				function(t) {
					var r, i;
					return i = Math.max(t - e, 0),
					r = t + 1,
					o[t] = Math.max.apply(Math, l[n].slice(i, r))
				})
			},
			r
		}
		function $(t, n, e, r, u) {
			function l(t) {
				return [t, b[t]]
			}
			var a, s, c, h, d, m, y, _, x, $, w, b, k, T, M, C, S;
			return null == e && (e = 12),
			null == r && (r = 26),
			null == u && (u = 9),
			b = t[I],
			x = t[P],
			k = l(v(t, n, e)),
			s = k[0],
			w = k[1],
			T = l(v(t, n, r)),
			a = T[0],
			$ = T[1],
			M = l(p(t,
			function(t) {
				return w[t] - $[t]
			})),
			h = M[0],
			_ = M[1],
			C = l(v(t, h, u)),
			c = C[0],
			y = C[1],
			S = i(t),
			d = S[0],
			m = S[1],
			x[d] = function(n) {
				return f(t, [s, a, h, c], n),
				g(t, n,
				function(t) {
					return m[t] = 2 * (_[t] - y[t])
				})
			},
			x[d](0),
			o(t, n, d),
			[h, c, d]
		}
		function w(t, n, e, r, u, l) {
			function a(t) {
				return j.push(t),
				[t, X[t]]
			}
			var s, c, h, d, g, v, $, w, b, k, T, M, C, S, F, A, B, D, R, O, N, E, L, W, U, G, H, q, z, Y, j, J, X, K, Q, V, Z, tn, nn, en, rn, on, un, ln, an, sn, cn, fn;
			return null == e && (e = 14),
			null == r && (r = 14),
			null == u && (u = 3),
			null == l && (l = 3),
			X = t[I],
			E = t[P],
			j = [],
			R = X[n],
			K = a(p(t,
			function(t) {
				var n;
				return null != (n = R[t - 1]) ? n: R[t]
			})),
			w = K[0],
			U = K[1],
			rn = a(p(t,
			function(t) {
				return Math.max(R[t] - U[t], 0)
			})),
			s = rn[0],
			F = rn[1],
			on = a(p(t,
			function(t) {
				return Math.abs(R[t] - U[t])
			})),
			h = on[0],
			B = on[1],
			un = a(y(t, s, e, 1)),
			c = un[0],
			A = un[1],
			ln = a(y(t, h, e, 1)),
			d = ln[0],
			D = ln[1],
			an = a(p(t,
			function(t) {
				return 0 === D[t] ? 100 : 100 * (A[t] / D[t])
			})),
			S = an[0],
			J = an[1],
			sn = a(_(t, S, r)),
			b = sn[0],
			G = sn[1],
			cn = a(x(t, S, r)),
			v = cn[0],
			L = cn[1],
			fn = a(p(t,
			function(t) {
				return J[t] - G[t]
			})),
			T = fn[0],
			q = fn[1],
			Q = a(p(t,
			function(t) {
				return L[t] - G[t]
			})),
			C = Q[0],
			Y = Q[1],
			V = a(m(t, T, u)),
			k = V[0],
			H = V[1],
			Z = a(m(t, C, u)),
			M = Z[0],
			z = Z[1],
			tn = a(p(t,
			function(t) {
				return 0 === z[t] ? 100 : 100 * (H[t] / z[t])
			})),
			$ = tn[0],
			W = tn[1],
			nn = a(m(t, $, l)),
			g = nn[0],
			N = nn[1],
			en = i(t),
			S = en[0],
			O = en[1],
			E[S] = function(n) {
				return f(t, j, n)
			},
			E[S](0),
			o(t, n, S),
			[$, g]
		}
		function b(t, n, e, r, u) {
			function l(t) {
				return R.push(t),
				[t, N[t]]
			}
			var a, s, c, h, d, g, m, v, $, w, b, k, T, M, C, S, F, A, B, D, R, O, N, E, L, W, U, G, H, q;
			return h = n[0],
			$ = n[1],
			a = n[2],
			null == e && (e = 9),
			null == r && (r = 3),
			null == u && (u = 3),
			N = t[I],
			T = t[P],
			R = [],
			D = N[$],
			b = N[a],
			C = N[h],
			E = l(_(t, $, e)),
			v = E[0],
			B = E[1],
			L = l(x(t, h, e)),
			c = L[0],
			M = L[1],
			W = l(p(t,
			function(t) {
				return M[t] - B[t] < 1e-8 ? 100 : 100 * ((b[t] - B[t]) / (M[t] - B[t]))
			})),
			w = W[0],
			O = W[1],
			U = l(y(t, w, r, 1)),
			g = U[0],
			F = U[1],
			G = l(y(t, g, u, 1)),
			s = G[0],
			k = G[1],
			H = l(p(t,
			function(t) {
				return 3 * F[t] - 2 * k[t]
			})),
			d = H[0],
			S = H[1],
			q = i(t),
			m = q[0],
			A = q[1],
			T[m] = function(n) {
				return f(t, R, n)
			},
			T[m](0),
			o(t, h, m),
			o(t, $, m),
			o(t, a, m),
			[g, s, d]
		}
		function k(t, n) {
			var e, r, u, l, a, s, c, f, h, d, p, m, v, y;
			for (l = n[0], r = n[1], u = n[2], e = n[3], d = t[I], c = t[P], h = [], v = i(t), a = v[0], s = v[1], c[a] = function(n) {
				return g(t, n,
				function(t) {
					return s[t] = parseFloat(((d[r][t] + d[u][t] + d[e][t]) / 3).toFixed(8)),
					s[t]
				})
			},
			c[a](0), y = arguments[1], p = 0, m = y.length; m > p; p++) f = y[p],
			o(t, f, a);
			return [a]
		}
		function T(t, n) {
			var e, r, u, l, a, s, c, f, h, d, p, m, v, y;
			for (l = n[0], r = n[1], u = n[2], e = n[3], d = t[I], c = t[P], h = [], v = i(t), a = v[0], s = v[1], c[a] = function(n) {
				return g(t, n,
				function(t) {
					return s[t] = parseFloat(((d[r][t] + d[u][t]) / 2).toFixed(8)),
					s[t]
				})
			},
			c[a](0), y = arguments[1], p = 0, m = y.length; m > p; p++) f = y[p],
			o(t, f, a);
			return [a]
		}
		function M(t, n) {
			return u(t, n, m.apply(null, arguments))
		}
		function C(t, n) {
			return u(t, n, v.apply(null, arguments))
		}
		function S(t, n) {
			var e, r, o, u;
			return o = t[I],
			u = i(t),
			e = u[0],
			r = u[1],
			o[e] = n,
			e
		}
		function F() {
			var t, n, e, r, i, o, u, l;
			for (e = arguments[0], r = 3 <= arguments.length ? ve.call(arguments, 1, i = arguments.length - 1) : (i = 1, []), n = arguments[i++], l = [], o = 0, u = n.length; u > o; o++) t = n[o],
			"object" == typeof t && t.length ? l.push(e.apply(null, ve.call(r).concat(ve.call(t)))) : l.push(e.apply(null, ve.call(r).concat([t])));
			return l
		}
		function A() {
			return F.apply(null, [h].concat(ve.call(arguments)))
		}
		function B(t, n, e, r) {
			var i;
			return i = h(t, r),
			i.slice(n, +e + 1 || 9e9)
		}
		var D, P, I, R, en, rn, on, un, ln, an, sn;
		return ln = s(3),
		I = ln[0],
		D = ln[1],
		P = ln[2],
		e = I,
		t = D,
		n = P,
		sn = c(en = function(t, n, e, r) {
			return t[I][e][n] = r
		}),
		rn = c(function(t, n, e) {
			return t[I][e].push(n)
		}),
		un = c(R = function(t, n, e, r) {
			return t[I][e].splice(n, 0, r)
		}),
		on = c(function(t, n, e) {
			return t[I][e].splice(n, 1)
		}),
		an = sn,
		X = r,
		tn = sn,
		z = on,
		K = un,
		Q = l,
		nn = a,
		O = S,
		N = C,
		L = M,
		W = $,
		G = w,
		E = b,
		U = T,
		H = k,
		j = d,
		V = an,
		Y = h,
		q = F,
		J = A,
		Z = B
	} (),
	fn = gn = pn = cn = hn = sn = un = ln = an = dn = null,
	i = u = o = l = null,
	function() {
		function t(t, n) {
			return t[0] - n[0]
		}
		function n(t) {
			var n;
			return n = {
				group: t
			},
			e(n),
			n
		}
		function e(n) {
			return n[m] = new A({
				compare: t
			}),
			n[y] = new A({
				compare: t
			}),
			n[v] = new A({
				compare: t
			}),
			n[_] = new A({
				compare: t
			}),
			n
		}
		function r(t, n, e) {
			var r, i;
			return t[n].insert(e),
			e[w] ? (e = [parseInt(e[x] / t.group) * t.group, e[w]], i = n === y ? _: v, (r = t[i].find(e)) ? r[w] += e[w] : (r = e, t[i].insert(r))) : void 0
		}
		function a(t, n, e) {
			var r, i;
			if (e && (t[n]["delete"](e), e[w])) return e = [parseInt(e[x] / t.group) * t.group, e[w]],
			i = n === y ? _: v,
			r = t[i].find(e),
			r && (r[w] -= e[w], r[w] < 1e-12) ? t[i]["delete"](r) : void 0
		}
		function c(t, n) {
			var e, i, o, u, l, s, c, f, h, d, p;
			if (d = n.type_str, c = n.price_int, f = n.total_volume_int, i = n.now, h = "bid" === d ? y: m, l = t[h], p = parseInt(f), s = parseInt(c), o = [s, p, i], u = l.find([s]), a(t, h, u), p && r(t, h, o), h === y) for (; (e = t[m].get(0)) && e[x] <= s;) a(t, m, e);
			else for (; (e = t[y].get( - 1)) && e[x] >= s;) a(t, y, e);
			return t
		}
		function f(t, n, e) {
			var i, o, u, l, s, c, f, h;
			if (null == e && (e = !0), c = n[0], i = n[1], h = n[2], f = "bid" === h ? y: m, s = t[f], u = [c, i], l = s.find([c]), a(t, f, l), i && r(t, f, u), !e) return t;
			if (f === y) for (; (o = t[m].get(0)) && o[x] <= c;) a(t, m, o);
			else for (; (o = t[y].get( - 1)) && o[x] >= c;) a(t, y, o);
			return t
		}
		function h(t, n) {
			var e, i, o, u, l, s;
			if (u = n.price, e = n.amount, l = n.trade_type, "bid" === l) for (s = m, o = t[s]; e > 1e-12 && (i = t[m].get(0)) && i[0] <= u;) {
				if (i[1] > e) {
					a(t, s, i),
					i[1] = i[1] - e,
					r(t, s, i);
					break
				}
				a(t, m, i),
				e -= i[1]
			} else for (s = y; e > 1e-12 && (i = t[y].get( - 1)) && i[0] >= u;) {
				if (i[1] > e) {
					a(t, s, i),
					i[1] = i[1] - e,
					r(t, s, i);
					break
				}
				a(t, y, i),
				e -= i[1]
			}
			return t
		}
		function d(t, n) {
			var e, r, i;
			for (e = 0; (i = t[m].get(0)) && i[x] < n;)++e,
			a(t, m, i);
			for (r = 0; (i = t[y].get( - 1)) && i[x] > n;)++r,
			a(t, y, i);
			return [e, r]
		}
		function p(t, n, e, r) {
			var i, o, u, l, s, c;
			for (i = 0, o = 0, s = 0, c = 0, u = 0; (l = t[m].at(u)) && l[x] <= e;) l[$] >= r ? (++u, ++s) : (++i, a(t, m, l));
			for (u = -1; (l = t[y].at(u)) && l[x] >= n;) l[$] >= r ? (--u, ++c) : (++o, a(t, y, l));
			return [i, o, s, c]
		}
		function g(t) {
			var n, e, r, i;
			return n = t[m],
			r = t[y],
			e = n.slice( - 11, -1),
			i = r.slice(0, 10)
		}
		var m, v, y, _, x, $, w, b, k;
		return b = s(4),
		y = b[0],
		m = b[1],
		_ = b[2],
		v = b[3],
		k = [0, 1, 2],
		x = k[0],
		w = k[1],
		$ = k[2],
		fn = n,
		gn = c,
		pn = f,
		cn = g,
		hn = r,
		sn = a,
		un = d,
		ln = p,
		an = e,
		dn = h,
		i = m,
		u = y,
		o = v,
		l = _
	} (),
	Sn = drawVolumeBar = drawOpenCloseBar = kn = bn = drawHighLowLine = Cn = null,
	periodOptions = vn = null,
	_n = yn = $n = wn = xn = Tn = Mn = null,
	function() {
		function volumeBar(t, n, r, i, o, u) {
			var l, a, s;
			return s = he(n, i, o),
			l = s[0],
			a = s[1],
			a > r ? _bar(t, l, r, u, a - r) : _bar(t, l, a, u, r - a)
		}
		function openCloseBar(t, n, r, i, o, u) {
			var l, a, s;
			return l = de(n, r),
			a = pe(n, i),
			s = pe(n, o),
			_bar(t, l, s, u, a - s)
		}
		function _bar(t, n, e, r, i) {
			return 0 > i && (e += i, i = -i),
			0 === i && (i = 1),
			t.fillStyle === t.strokeStyle ? t.fillRect(n, e, r, i) : i > 1 ? (t.fillRect(n, e, r, i), t.strokeRect(n + .5, e + .5, r - 1, i - 1)) : 1 === i ? (t.beginPath(), t.moveTo(n, e + .5), t.lineTo(n + r, e + .5), t.stroke()) : void 0
		}
		function r(t, n, e, r) {
			var i, o, u, l, a, s, c;
			for (t.beginPath(), u = a = 0, s = e.length; s > a; u = ++a) l = e[u],
			c = he(n, u, l),
			i = c[0],
			o = c[1],
			r && (i += r),
			u ? t.lineTo(i, o) : t.moveTo(i, o);
			return t.stroke()
		}
		function i(t, n, e, r, i) {
			return t.beginPath(),
			t.moveTo(n, e),
			t.lineTo(n, e - i),
			t.lineTo(n + .866 * i, e - .5 * i),
			t.fill()
		}
		function o(t, n, e, r, i) {
			return t.beginPath(),
			t.moveTo(n, e),
			t.lineTo(n, e - i),
			t.lineTo(n - .866 * i, e - .5 * i),
			t.fill()
		}
		function highLowLine(t, n, e, r, i, o) {
			var u, l, a;
			return u = de(n, e),
			l = pe(n, r),
			a = pe(n, i),
			o && (u += o),
			t.beginPath(),
			t.moveTo(u + .5, a),
			t.lineTo(u + .5, l),
			t.stroke()
		}
		function l(t, n, e, r) {
			return t.beginPath(),
			t.moveTo(e, n),
			t.lineTo(r, n),
			t.stroke()
		}
		function a(t, n, e, r) {
			return t.beginPath(),
			t.moveTo(n, e),
			t.lineTo(n, r),
			t.stroke()
		}
		function s(t, n, e) {
			var r, i, o, u, a, s, c, f;
			for (r = en(n[D]), u = en(n[P]), n = ge(r, u), s = e(r, u), c = 0, f = s.length; f > c; c++) a = s[c],
			o = pe(n, a),
			i = r.w,
			t.fillText(a, i - 8, o + .5),
			l(t, o + .5, r.w - 4, r.w);
			return null
		}
		function c(t, n) {
			return s(t, n,
			function(t, n) {
				var e, r, i, o, u, l, a;
				for (r = Math.floor(t.h / 32), u = n.h / r, o = n.y, a = [], e = l = 0; r >= 0 ? r >= l: l >= r; e = r >= 0 ? ++l: --l) i = o + e * u,
				a.push(parseFloat(i.toPrecision(5)));
				return a
			})
		}
		function f(t, n) {
			return s(t, n,
			function(t, n) {
				var e, r, i, o, u, l, a, s, c;
				for (i = Math.abs(t.h / 32), l = n.h / i, c = function() {
					var t, n, i, o;
					for (i = [1, 2, 5], o = [], t = 0, n = i.length; n > t; t++) e = i[t],
					r = l / e,
					s = Math.ceil(Math.log(r) / Math.log(10)).toFixed(2),
					s = Math.pow(10, s),
					s = e * s,
					o.push(s);
					return o
				} (), a = Math.min.apply(Math, c), o = Math.ceil(n.y / a) * a, u = []; o < n.y + n.h;) u.push(parseFloat(o.toPrecision(5))),
				o += a;
				return u
			}),
			null
		}
		function h(t, n, e, r) {
			var i, o, u, a, s, c, f, h;
			for (i = en(n[D]), a = en(n[P]), n = ge(i, a, n[I]), c = r(i, a), t.textAlign = "center", f = 0, h = c.length; h > f; f++) s = c[f],
			u = pe(n, s),
			o = i.x,
			e && "text" !== e || t.fillText(s, o + 50, u + .5),
			e && "hr" !== e || (l(t, u + .5, o, o + 6), l(t, u + .5, o + i.w - 6, o + i.w));
			return t.textAlign = "left",
			null
		}
		function d(t, n) {
			return h(t, n, null,
			function(t, n) {
				var e, r, i, o, u, l, a;
				for (r = Math.floor(t.h / 32), u = n.h / r, o = n.y, a = [], e = l = 0; r >= 0 ? r >= l: l >= r; e = r >= 0 ? ++l: --l) i = o + e * u,
				a.push(parseFloat(i.toPrecision(5)));
				return a
			})
		}
		function p(t, n, e) {
			return h(t, n, null,
			function() {
				return e
			})
		}
		function g(t, n, e) {
			return h(t, n, e,
			function(t, n) {
				var e, r, i, o, u, l, a, s, c;
				for (i = Math.abs(t.h / 32), l = n.h / i, c = function() {
					var t, n, i, o;
					for (i = [1, 2, 5], o = [], t = 0, n = i.length; n > t; t++) e = i[t],
					r = l / e,
					s = Math.ceil(Math.log(r) / Math.log(10)).toFixed(2),
					s = Math.pow(10, s),
					s = e * s,
					o.push(s);
					return o
				} (), a = Math.min.apply(Math, c), o = Math.ceil(n.y / a) * a, u = []; o < n.y + n.h;) u.push(parseFloat(o.toPrecision(5))),
				o += a;
				return u
			}),
			null
		}
		function m(t, n) {
			var e;
			return t.save(),
			t.beginPath(),
			e = en(n[D]),
			e.y += 8,
			e.h -= 16,
			t.moveTo(e.x, e.y),
			t.lineTo(e.x + e.w, e.y),
			t.lineTo(e.x + e.w, e.y + e.h),
			t.lineTo(e.x, e.y + e.h),
			t.clip()
		}
		function v(t, n, e) {
			return m(t, n),
			e(),
			t.restore()
		}
		return Sn = r,
		drawVolumeBar = volumeBar,
		drawOpenCloseBar = openCloseBar,
		drawBar = _bar,
		bn = l,
		drawHighLowLine = highLowLine,
		Cn = a,
		periodOptions = c,
		vn = f,
		_n = d,
		yn = g,
		$n = m,
		wn = v,
		xn = p,
		Tn = o,
		Mn = i
	} (),
	A = null,
	function() {
		var t;
		return t = function() {
			function t(t) {
				this.options = null != t ? t: {},
				this.id = ++o,
				this.min = 0,
				this.max = 0,
				this.count = 0,
				this.type = i,
				this.total = 0,
				this.parent = null,
				this.children = [],
				this.next = null,
				this.prev = null,
				this.compare = this.options.compare,
				this.multimap = this.options.multimap,
				null == this.compare && (this.compare = function(t, n) {
					return t - n
				})
			}
			var n, e, r, i, o;
			return n = 8,
			e = n << 1,
			r = 0,
			i = 1,
			o = 0,
			t.prototype.insert_value_ = function(t) {
				var n, e, r, i, o, u, l;
				for (e = this.count, n = this.children, r = l = 0; e >= 0 ? e > l: l > e; r = e >= 0 ? ++l: --l) {
					if (u = n[r], o = this.compare(u, t), 0 === o) {
						if (this.multimap) break;
						return
					}
					if (o > 0) break
				}
				if (0 === r) for (this.min = t, i = this; (i = i.parent) && this.compare(i.min, t) > 0;) i.min = t;
				if (r === e) for (this.max = t, i = this; (i = i.parent) && this.compare(i.max, t) < 0;) i.max = t;
				for (this.children.splice(r, 0, t), this.count += 1, i = this; i;) i.total += 1,
				i = i.parent;
				return this.rebuild_(),
				this
			},
			t.prototype.insert_node_ = function(t, n) {
				var e, r, i;
				for (e = this.count, r = i = 0; (e >= 0 ? e > i: i > e) && this.children[r].min !== t; r = e >= 0 ? ++i: --i);
				return n.parent = this,
				this.count += 1,
				this.children.splice(r + 1, 0, n),
				this.rebuild_()
			},
			t.prototype.find_node_ = function(t) {
				var n, e, i, o, u;
				for (o = this; o.type === r;) {
					if (n = o.children, e = o.count, this.compare(t, n[0].min) <= 0) i = 0;
					else if (this.compare(t, n[e - 1].max) >= 0) i = e - 1;
					else for (i = u = 0; (e >= 0 ? e > u: u > e) && !(this.compare(n[i].max, t) >= 0); i = e >= 0 ? ++u: --u);
					o = n[i]
				}
				return o
			},
			t.prototype.has = function(t) {
				var n;
				return n = this.find_node_(t),
				-1 !== n.children.indexOf(t)
			},
			t.prototype.replace_value = function(t) {
				var n, e, r, i, o, u;
				for (r = this.find_node_(t), n = r.children, e = o = 0, u = n.length; u > o; e = ++o) i = n[e],
				0 === this.compare(i, t) && (n[e] = t);
				return this
			},
			t.prototype.get_node_ = function(t) {
				var n, e, i, o, u;
				if (i = this, t >= this.total) return [null, null];
				if (0 > t) return [null, null];
				for (; i.type === r;) for (e = i.children, o = 0, u = e.length; u > o; o++) {
					if (n = e[o], !(t >= n.total)) {
						i = n;
						break
					}
					t -= n.total
				}
				return [i, t]
			},
			t.prototype.set_min_ = function(t) {
				var n, e;
				for (e = this, n = this.min; e && 0 === this.compare(e.min, n);) e.min = t,
				e = e.parent;
				return this
			},
			t.prototype.set_max_ = function(t) {
				var n, e;
				for (e = this, n = this.max; e && 0 === this.compare(e.max, n);) e.max = t,
				e = e.parent;
				return this
			},
			t.prototype.inc_total_ = function() {
				var t;
				for (t = this; t;) t.total += 1,
				t = t.parent;
				return this
			},
			t.prototype.dec_total_ = function() {
				var t;
				for (t = this; t;) t.total -= 1,
				t = t.parent;
				return this
			},
			t.prototype.clean_node_ = function() {
				var t, n;
				return this.parent ? (this.parent.delete_node_(this), this.type === i ? (null != (t = this.prev) && (t.next = this.next), null != (n = this.next) ? n.prev = this.prev: void 0) : void 0) : this.type = i
			},
			t.prototype.delete_node_ = function(t) {
				var n;
				return n = this.children.indexOf(t),
				this.children.splice(n, 1),
				this.count -= 1,
				0 === this.count ? this.clean_node_() : (0 === n && this.set_min_(this.children[0].min), n === this.count ? this.set_max_(this.children[this.count - 1].max) : void 0)
			},
			t.prototype.delete_value_ = function(t) {
				var n, e;
				return n = this.children,
				e = this.indexOf_(t),
				-1 !== e && (n.splice(e, 1), this.count -= 1, this.dec_total_(), 0 === this.count ? this.clean_node_() : (0 === e && this.set_min_(n[0]), e === this.count && this.set_max_(n[this.count - 1]))),
				this
			},
			t.prototype.rebuild_ = function() {
				var t, o, u;
				if (! (this.count < e)) return null != this.parent ? (o = this.slice_(n, e - 1), o.parent = this.parent, this.count = n, this.total = this.total - o.total, this.children.splice(n, n), this.max = this.type === i ? this.children[n - 1] : this.children[n - 1].max, this.parent.insert_node_(this.min, o), this.type === i && (this.next && (this.next.prev = o), o.next = this.next, this.next = o)) : (t = this.slice_(0, n - 1), u = this.slice_(n, e - 1), t.parent = this, u.parent = this, t.next = u, u.prev = t, this.count = 2, this.children = [t, u], this.type = r),
				this
			},
			t.prototype.slice_ = function(n, e) {
				var r, o, u, l, a, s, c, f;
				if (u = e - n + 1, l = new t(this.options), l.count = u, l.type = this.type, o = this.children, this.type === i) l.min = o[n],
				l.max = o[e],
				l.children = o.slice(n, +e + 1 || 9e9),
				l.total = u;
				else {
					for (l.min = o[n].min, l.max = o[e].max, l.children = o.slice(n, +e + 1 || 9e9), a = 0, f = l.children, s = 0, c = f.length; c > s; s++) r = f[s],
					r.parent = l,
					a += r.total;
					l.total = a
				}
				return l
			},
			t.prototype.atom = function() {
				var t;
				for (t = this; t.type === r;) t = t.children[0];
				return t
			},
			t.prototype.indexOf_ = function(t) {
				var n, e, r, i, o, u;
				for (u = this.children, n = i = 0, o = u.length; o > i; n = ++i) {
					if (r = u[n], e = this.compare(r, t), 0 === e) return n;
					if (e > 0) return - 1
				}
				return - 1
			},
			t.prototype.insert = function(t) {
				var n;
				return n = this.find_node_(t),
				n.insert_value_(t),
				this
			},
			t.prototype["delete"] = function(t) {
				var n;
				return n = this.find_node_(t),
				n.delete_value_(t)
			},
			t.prototype.replace = function(t) {
				return this["delete"](t),
				this.insert(t)
			},
			t.prototype.get = function(t) {
				var n, e, r;
				return 0 > t && (t += this.size()),
				r = this.get_node_(t),
				n = r[0],
				e = r[1],
				n ? n.children[e] : null
			},
			t.prototype.at = function(t) {
				var n, e, r;
				return 0 > t && (t += this.size()),
				r = this.get_node_(t),
				n = r[0],
				e = r[1],
				n ? n.children[e] : null
			},
			t.prototype.find = function(t) {
				return this.find_all(t)[0]
			},
			t.prototype.find_all = function(t) {
				var n, e, r, i, o, u, l;
				if (i = [], n = this.find_node_(t), this.compare(t, n.min) < 0) return [];
				if (this.compare(t, n.max) > 0) return [];
				for (l = n.children, o = 0, u = l.length; u > o; o++) if (r = l[o], e = this.compare(r, t), 0 === e) i.push(r);
				else if (e > 0) break;
				return i
			},
			t.prototype.slice = function(t, n) {
				var e, r, i, o, u;
				if (null == n && (n = this.total - 1), 0 > t && (t += this.total), 0 > n && (n += this.total), 0 > t && (t = 0), n >= this.total && (n = this.total - 1), u = this.get_node_(t), r = u[0], o = u[1], !r) return [];
				for (i = n - t + 1, e = []; i && r;) o < r.count ? (e.push(r.children[o++]), --i) : (r = r.next, o = 0);
				return e
			},
			t.prototype.flatten = function() {
				var t, n, e, r, i, o;
				for (r = [], i = this.atom(); i;) {
					for (n = i.count, t = i.children, e = o = 0; n >= 0 ? n > o: o > n; e = n >= 0 ? ++o: --o) r.push(t[e]);
					i = i.next
				}
				return r
			},
			t.prototype.dump = function(t) {
				var n, e, r, o, u, l, a;
				for (null == t && (t = 0), r = process.stdout, n = o = 0, a = this.count; a >= 0 ? a > o: o > a; n = a >= 0 ? ++o: --o) if (this.type === i) {
					for (e = u = 0; t >= 0 ? t > u: u > t; e = t >= 0 ? ++u: --u) r.write(" ");
					r.write(this.children[n] + " ")
				} else this.children[n].dump(t + 1);
				for (e = l = 0; t >= 0 ? t > l: l > t; e = t >= 0 ? ++l: --l) r.write(" ");
				return r.write("min: " + this.min + " max: " + this.max + " count: " + this.count + "/" + this.total + "\n"),
				this
			},
			t.prototype.delete_if = function() {},
			t.prototype.size = function() {
				return this.total
			},
			t
		} (),
		A = t,
		null != Zn && (A = A),
		"undefined" != typeof module && null !== module ? module.exports = A: void 0
	} (),
	Un = Gn = Hn = Nn = En = Ln = qn = zn = updateLastBarTimePassed = null,
	resizeBlocks = null,
	a = null,
	Rn = null,
	function() {
		function t(t) {
			var n;
			return n = t.getHours()
		}
		function n(t) {
			var n;
			return n = t.getMinutes(),
			"" + n + "min"
		}
		function e(t) {
			return c[t.getMonth()]
		}
		function r(t) {
			var n, e;
			return e = t.getMonth(),
			n = t.getDate(),
			"" + c[e] + " " + n
		}
		function i(t) {
			return t.getHours() + ":" + t.getMinutes()
		}
		function o(t) {
			return Rn(t.getHours()) + ":" + Rn(t.getMinutes()) + ":" + Rn(t.getSeconds())
		}
		function u(t) {
			var n, e, r, i, o;
			return n = t.getFullYear(),
			o = Rn(t.getMonth() + 1),
			e = Rn(t.getDate()),
			r = Rn(t.getHours()),
			i = Rn(t.getMinutes()),
			"" + n + "-" + o + "-" + e + " " + r + ":" + i
		}
		function l(t) {
			var n, e, r, i, o, u, l, a;
			return e = t.getFullYear(),
			u = t.getMonth() + 1,
			r = t.getDate(),
			i = Rn(t.getHours()),
			o = Rn(t.getMinutes()),
			l = Rn(t.getSeconds()),
			a = f[t.getDay()],
			n = c[t.getMonth()],
			"" + a + ", " + n + " " + r + lang._day + " " + i + ":" + o + ":" + l
		}
		function s(t) {
			var n, e, r, i, o, u, l, a;
			for (i = [[86400, 86400, lang._dt], [3600, 3600, lang._hr], [60, 60, lang._mn], [0, 1, lang._sc]], u = 0, l = i.length; l > u; u++) if (a = i[u], n = a[0], e = a[1], r = a[2], t >= n) return o = parseFloat((t / e).toFixed(1)),
			o > 1 ? o + " " + r + lang.ago: o + " " + r + lang.ago;
			return null
		}
		function _resizeBlocks() {
			depth_block_height = ($(window).height() - $(".navbar").height() - $("#footer").height()) / 2,
			$("#chart_depth_block").height(depth_block_height),
			$("#chart_depth_block .scroll").height(depth_block_height),
			trade_block_height = ($(window).height() - $(".navbar").height() - $("#footer").height()) / 2-40,
			$("#chart_trade_block").height(trade_block_height),
			$("#setting_block").height(trade_block_height - $("#alert_block").height() - 15),
			//$("#alert_block").css("top", trade_block_height - $("#alert_block").height()),
			$(".nice-scroll").niceScroll({
				cursorborder: "1px solid #080808",
				cursorwidth: 9,
			})
		}
		var c, f;
		return c = lang.months.split(" "),
		f = lang.weekdays.split(" "),
		Rn = function(t) {
			return t = t.toString(),
			1 === t.length ? "0" + t: t
		},
		Un = t,
		Gn = n,
		Hn = e,
		Nn = r,
		En = u,
		Ln = l,
		qn = i,
		zn = o,
		updateLastBarTimePassed = s,
		a = f,
		Rn = Rn,
		resizeBlocks = _resizeBlocks
	} (),
	Qn = Vn = Xn = Kn = null,
	function() {
		function t(n, e) {
			var r;
			return n[0] && n[0].length ? (n = function() {
				var i, o, u;
				for (u = [], i = 0, o = n.length; o > i; i++) r = n[i],
				u.push(t(r, e));
				return u
			} (), t(n, e)) : e.apply(null, n)
		}
		function n(n) {
			return t(n, Math.max)
		}
		function e(n) {
			return t(n, Math.min)
		}
		function r(t) {
			var e, r;
			return r = function() {
				var n, i, o;
				for (o = [], n = 0, i = t.length; i > n; n++) r = t[n],
				o.push(function() {
					var t, n, i;
					for (i = [], t = 0, n = r.length; n > t; t++) e = r[t],
					i.push(Math.abs(e));
					return i
				} ());
				return o
			} (),
			n(r)
		}
		return Qn = Vn = Xn = null,
		Qn = n,
		Vn = e,
		Xn = r,
		Kn = t
	} (),
	f = v = x = null,
	w = g = v = _ = h = m = y = b = d = p = null,
	ie = ee = re = null,
	function() {
		function t() {
			var t, n;
			return n = {},
			t = X(l),
			n[i] = t,
			n[A] = 0,
			n
		}
		function n(t, n) {
			var e, s, f, h, d, p, g, m, v, y, _, x;
			if (e = t[i], n = en(n), n[C] = n[C] - n[C] % t[A], y = r(t, n[C]), f = y[0], h = y[1], f) return d = j(e, h, l),
			d[a] > n[S] && (d[F] = n[M], d[a] = n[S]),
			d[$] < n[S] && (d[o] = n[M], d[$] = n[S]),
			d[c] < n[M] && (d[c] = n[M]),
			d[T] > n[M] && (d[T] = n[M]),
			d[D] += n[k],
			nn(e, h, d);
			for (d = {},
			d[B] = n[C], _ = [a, $], p = 0, m = _.length; m > p; p++) s = _[p],
			d[s] = n[S];
			for (x = [F, o, c, T], g = 0, v = x.length; v > g; g++) s = x[g],
			d[s] = n[M];
			return d[D] = n[k],
			d[u] = new Date(1e3 * d[B]),
			Q(e, h, d)
		}
		function e(n, e) {
			var r, l, s, f, h, d, p, g, m, v, y, _, x;
			for (s = t(), r = s[i], f = d = 0, m = e.length; m > d; f = ++d) {
				for (h = e[f], h = en(h), h[B] = parseInt(h[B]), _ = [F, o, c, T], p = 0, v = _.length; v > p; p++) l = _[p],
				h[l] = parseFloat(h[l]);
				for (x = [B, a, $], g = 0, y = x.length; y > g; g++) l = x[g],
				h[l] = parseInt(h[l]);
				h[D] = parseFloat(h[D]),
				h[u] = new Date(1e3 * h[B]),
				Q(r, f, h)
			}
			return s[A] = parseInt(n),
			s
		}
		function r(t, n) {
			var e, r, o;
			if (e = t[i], !(o = Y(e, B))) return [!1, 0];
			for (r = o.length; r--;) if (! (o[r] > n)) {
				if (o[r] < n) break;
				return [!0, r]
			}
			return [!1, r + 1]
		}
		var i, o, u, l, a, c, $, T, F, A, B, D, P, I;
		return P = s(3),
		i = P[0],
		$ = P[1],
		A = P[2],
		f = i,
		v = $,
		x = A,
		I = [0, 1, 2, 3, 4, 5, 6, 7, 8],
		B = I[0],
		a = I[1],
		$ = I[2],
		F = I[3],
		o = I[4],
		c = I[5],
		T = I[6],
		D = I[7],
		u = I[8],
		l = I,
		w = B,
		g = a,
		v = $,
		_ = F,
		h = o,
		m = c,
		y = T,
		b = D,
		d = u,
		p = l,
		ie = t,
		ee = n,
		re = e
	} (),
	S = M = k = C = T = F = null,
	ae = le = null,
	function() {
		function t(t) {
			var n;
			return n = {},
			n[u] = parseInt(t.tid),
			n[i] = parseFloat(t.price),
			n[e] = parseFloat(t.amount),
			n[o] = parseInt(t.date),
			n[r] = Date.now(),
			n[l] = t.trade_type,
			n
		}
		function n(t) {
			var n;
			return n = {},
			n[u] = parseInt(t.tid),
			n[i] = parseFloat(t.price),
			n[e] = parseFloat(t.amount),
			n[o] = parseInt(t.date),
			n[r] = Date.now(),
			n[l] = t.trade_type,
			n
		}
		var e, r, i, o, u, l, a;
		return a = s(7),
		u = a[0],
		i = a[1],
		e = a[2],
		o = a[3],
		r = a[4],
		l = a[5],
		S = u,
		M = i,
		k = e,
		C = o,
		T = r,
		F = l,
		ae = t,
		le = n
	} (),
	ge = ce = fe = historicalData = de = pe = he = null,
	D = P = I = null,
	function() {
		return function() {
			function t(t, n, e) {
				var r;
				return null == e && (e = !1),
				r = [],
				r[c] = en(t),
				r[f] = en(n),
				r[h] = e,
				r
			}
			function n(t, n) {
				var e, r;
				return e = t[c],
				r = t[f],
				(n - r.x) / r.w * e.w + e.x
			}
			function e(t, n) {
				var e, r, i, o, u, l;
				return e = t[c],
				r = t[f],
				t[h] ? (l = r.y, u = r.y + r.h, o = 0, i = Math.log(u / l), n = Math.log(n / l), (n - o) / i * e.h + e.y) : (n - r.y) / r.h * e.h + e.y
			}
			function r(t, e) {
				return Math.round(n(t, e))
			}
			function i(t, n) {
				return Math.round(e(t, n))
			}
			function o(t, n, e) {
				return [r(t, n), i(t, e)]
			}
			function u(t, e) {
				return Math.round(n(t, e)) + .5
			}
			function l(t, n) {
				return Math.round(e(t, n)) + .5
			}
			function a(t, n, e) {
				return [u(t, n), l(t, e)]
			}
			var c, f, h, d;
			return d = s(3),
			c = d[0],
			f = d[1],
			h = d[2],
			ge = t,
			ce = u,
			fe = l,
			historicalData = a,
			de = r,
			pe = i,
			he = o,
			D = c,
			P = f,
			I = h
		} ()
	} (),
	function() {
		$(function(t) {
			return t
		})
	} (),
	function() {
		return window.$theme_dark = {
			Background: "#0A0A0A",
			"Background Mask": "rgba(10, 10, 10, 0.8)",
			"Main Text": "#CCC",
			"Minor Text": "#333",
			"Highlight Text": "#FF0",
			Border: "#333",
			Link: "#36F",
			"Activated Link": "#F80",
			"Green Stroke": "#99CC66",	//green
//			"Green Stroke": "#2b7edc",	//old
			"Green Fill": "#658744",	//green
//			"Green Fill": "#4775a9",	//old
			"Red Stroke": "#CC1414",
			"Red Fill": "#990F0F",
			"Axis Background": "rgba(10, 10, 10, 0.8)",
			"Axis Key Text": "#FFFFFF",
			"Axis Text": "#999",
			"Green Arrow": "rgba(0,204,0,0.6)",
			"Red Arrow": "rgba(204,0,0,0.6)",
			"Arrow Text": "rgba(255,255,0,0.8)",
			Cross: "rgba(255,255,255,0.4)",
			"Stick Line": "#CCCC00",
			Colors: ["#A6CEE3", "#FDBF6F", "#DF8ADF", "#1F78B4", "#B2DF8A", "#FB9A99"],
			"Green Area": "rgba(64,255,64,0.3)",
			"Red Area": "rgba(255,64,64,0.3)",
			"Minor Arrow": "rgba(204,204,204,0.6)"
		},
		window.$theme_light = {
			Background: "#4775a9",
			"Background Mask": "#FBFAF8",
			"Main Text": "#333",
			"Minor Text": "#CCC",
			"Highlight Text": "#000",
			Border: "#eee",
			Link: "#0D86FF",
			"Activated Link": "#F80",
			"Green Stroke": "#99CC66",	//green
//			"Green Stroke": "#2b7edc",	//old
			"Green Fill": "#658744",	//green
//			"Green Fill": "#4775a9",	//old
			"Red Stroke": "#cc1414",
			"Red Fill": "#990f0f",
			"Axis Background": "#f7f6f5",
			"Axis Key Text": "#333",
			"Axis Text": "#666",
			"Red Arrow": "rgba(51,160,44,0.8)",
			"Green Arrow": "rgba(71,117,169,0.8)",
			"Arrow Text": "#000",
			Cross: "rgba(0,0,0,0.4)",
			"Stick Line": "#efefee",
			Colors: ["#24B324", "#D58E31", "#DF8ADF", "#822B82", "#B2DF8A", "#FB9A99"],
			"Green Area": "rgba(64,255,64,0.2)",
			"Red Area": "rgba(255,64,64,0.2)",
			"Minor Arrow": "#999"
		}
	} (),
	jn = null,
	function() {
		var t, n, e, r, a, c, g, x, w, A, B, O, H, q, z, J, X, K, Q, V, tn, nn, en, on, ln, sn, cn, hn, gn, periodOptions, vn, $n, Tn, Mn, Dn, Pn, In, Rn, Gn, Jn, Kn, Zn, te, ne, ie, historicalData, ce, fe, ye, _e, xe, $e, periodObject, be, ke, Te, Me, Ce, Se, Fe, Ae, Be, De, Pe, Ie, Re, Oe, Ne, Ee, Le, refreshInterval, Ue, Ge, He, qe, ze, Ye, je, Je, Xe, Ke, Qe, Ve, Ze, tr, nr, er, rr, ir, or, ur, lr, ar, sr, cr, fr, hr, dr, pr, gr, mr, vr, crossCanvas, mainCanvas, xr, $r, wr, br, kr, Tr, Mr, Cr, Sr, Fr, bannerBlockWidth, Br, logPrint, Pr, Ir, Rr, Or, Nr, Er, Lr, Wr, Ur, Gr, Hr, qr, zr, Yr, jr, Jr, rightBannerBlock, loadingDiv, Qr, Vr, Zr, ti, ni, rightBlock, ri, windowObj, oi, ui, li, ai, si, ci, fi, interval, intervals, pi, gi, mi, vi, yi, _i, xi, $i, wi;
		$(function() {
			function storeChartInfoIntoCookie(t, n, e) {
				var r;
				return null == e && (e = {}),
				r = "session" === e.mode ? {
					path: "/"
				}: {
					expires: 3650,
					path: "/"
				},
				$.cookie(t, n, r)
			}
			function ki(t, n) {
				return t > n
			}
			function Ti() {
				return a = true,
				showNotify("Cross is locked."),
				$("#main canvas").css("cursor", "default")
			}
			function Mi() {
				return a = false,
				showNotify("Cross is unlocked."),
				$("#main canvas").css("cursor", "none")
			}
			function drawChart() {
				var t, n, e, i, o, u, l, a, s, c;
				for (hr = windowObj.width() - rightBlock.width() - rightBannerBlock.width(), fr = windowObj.height() - ($(".navbar").height() + $("#footer").height()), Qr.height(fr), fr = fr - $("#header_outer").height()-20, oi.height(fr), c = [mainCanvas, crossCanvas], a = 0, s = c.length; s > a; a++) n = c[a],
				n.width = hr,
				n.height = fr;
				if (null == r)(r = Ur.outerHeight(!0)),
				i = r + 26 - fr;
				if (i > 0) {
					o = 15 - Math.ceil(i / 26),
					2 > o && (o = 2)
				} else o = 15,
				u = 13 * o,
				q = o,
				H = !1,
				H = !0,
				$("#orderbook .orderbook").height(u),
				$("#asks").css("margin-top", 13 * (o - 15)),
				$("#gasks").css("margin-top", 13 * (o - 15)),
				l = fr - Ur.outerHeight(!0),
				ri.height(l),
				Ir = Math.floor(fr / 6 - ne),
				lr = Math.floor((hr - or) / sr) + Math.floor(or / sr) - 1,
				lr = Math.floor((hr - or) / sr) - 1,
				ar = Math.floor((hr - or) / sr);
				if (ie) {
					t = ie[f],
					e = Y(t, h).length - 1,
					$e = e;
					if (null != Tr) Tr -= ar - li;
					else {
						Tr = e - ar + 1,
						0 > Tr && (Tr = 0)
					}
					li = ar,
					drawMainChart(),
					drawTickInfo(),
					!0
				}
			}
			function Si() {
				var t;
				return t = dr,
				crossCanvas.width = crossCanvas.width
			}
			function drawCrossOver(t, label, e, r, i, direction) {
				var u, l;
				return u = e,
				l = r,
				null == i && (i = t.measureText(label).width),
				t.beginPath(),
				t.textBaseline = "middle",
				"r" === direction ? (t.moveTo(u, l), t.lineTo(u - 5, l + 10.5), t.lineTo(u - 5 - i - 6 - 5 + .5, l + 10.5), t.lineTo(u - 5 - i - 6 - 5 + .5, l - 10.5), t.lineTo(u - 5, l - 10.5), t.lineTo(u, l), t.fill(), t.stroke(), t.fillStyle = Ie["Axis Text"], t.fillText(label, u - 5 - 3 - i, l)) : (t.moveTo(u, l), t.lineTo(u + 5, l + 10.5), t.lineTo(u + 5 + i + 6 + 5, l + 10.5), t.lineTo(u + 5 + i + 6 + 5, l - 10.5), t.lineTo(u + 5, l - 10.5), t.lineTo(u, l), t.fill(), t.stroke(), t.fillStyle = Ie["Axis Text"], t.fillText(label, u + 5 + 3, l))
			}
			function Ai(t) {
				var n;
				return t > 1e4 ? t.toFixed(0) : (n = t > 100 ? 5 : 4, t.toPrecision(n))
			}
			function drawTickInfo() {
				var n, e, r, o, u, l, a, s, c, v, x, $, k, T, M, C, S, F, A, B, R, O;
				if (ie && (x = dr, r = ie[f], $ = si > hr - or, $ ? (l = si, a = ci) : (l = gr, a = mr), n = $settings.stick_style.value, null != l)) {
					if (Si(), x.strokeStyle = Ie.Cross, bn(x, a + .5, 0, hr), Cn(x, l + .5, 0, fr), yi = j(r, Tr + kr, p), Tn) for (s = function(t) {
						var n, e, r, i, o, l, s, c, f, h, d;
						return n = t[P],
						t[I] ? (o = n.y, i = n.y + n.h, r = 0, e = Math.log(i / o), c = (a - u.y) / u.h * e + r, l = Math.exp(c) * o) : l = (a - u.y) / u.h * n.h + n.y,
						s = Ai(l),
						x.font = "12px Consolas, Monospace",
						x.fillStyle = Ie["Axis Text"],
						x.textAlign = "left",
						f = x.measureText(s).width,
						h = hr - or + (or - f - 8) / 2,
						d = a,
						x.strokeStyle = Ie.Cross,
						x.fillStyle = Ie["Background Mask"],
						drawCrossOver(x, s, hr - or + 20, d, f)
					},
					R = [Tn, $n, Oe], A = 0, B = R.length; B > A; A++) {
						C = R[A],
						C && (u = C[D], u.y + u.h < a && a < u.y && s(C))
					}
					$r && (Tr = wr - kr),
					(null != (O = t[i]) ? O.length: void 0) && $ && Fe ? (e = t, c = or - (hr - l) - 8, M = "", e[He][c] && (M += "Buy <span class=yellow>" + Ai(e[Je][c]) + "</span> " + w + " will cost <span class=green>" + Ai(e[He][c]) + "</span> " + g + " and price will reach <span class=green>" + e[ze][c] + "</span>.<br>"), e[qe][c] && (M += "Sell <span class=yellow>" + Ai(e[Je][c]) + "</span> " + w + " will receive <span class=red>" + Ai(e[qe][c]) + "</span> " + g + " and price will reach <span class=red>" + e[je][c] + "</span>.<br>"), e[Ye][c] && (M += "The average price of cost and receive is <span class=grey>" + Ai(e[Ye][c]) + "</span> " + be + "."), Hr.html(M)) : yi[h] && lr >= kr && (T = j(r, Tr + kr - 1, p), null == T && (T = yi), k = null != T[h] ? yi[h] / T[h] - 1 : 0, k = 100 * k, k = k.toFixed(2), "-" === k[0] ? F = "↘": k > 0 ? (k = "+" + k, F = "↗") : (k = "+" + k, F = "→"), F = "", M = [lang.date + ": " + En(yi[d]), lang.o + ": " + Ai(yi[_]), lang.h + ": " + Ai(yi[m]), lang.l + ": " + Ai(yi[y]), lang.c + ": " + Ai(yi[h]), lang.chg + ": " + k + " %", lang.amp + ": " + (100 * ((yi[m] - yi[y]) / yi[y])).toFixed(2) + " %", lang.v + ": " + yi[b].toFixed(2)].join("&nbsp;&nbsp;&nbsp;"), Hr.html(M), !Tn || "line" !== n && "line_o" !== n || (o = "m" === $settings.line_style.value ? parseFloat(((yi[m] + yi[y]) / 2).toFixed(8)) : yi[h], x.fillStyle = "", S = x.measureText(o).width + 8, v = 24, x.fillStyle = Ie["Background Mask"], x.strokeStyle = Ie.Border, a = pe(Tn, o), x.textAlign = "center", hr / 2 > l ? (drawBar(x, l + 4, a, S, v), x.fillStyle = Ie["Main Text"], x.fillText(o, l + 4 + S / 2, a + v / 2)) : (drawBar(x, l - 4, a, -S, v), x.fillStyle = Ie["Main Text"], x.fillText(o, l - 4 - S / 2, a + v / 2)), x.strokeStyle = Ie["Stick Line"], x.fillStyle = Ie["Stick Line"], x.beginPath(), x.arc(l + .5, a, 3, 0, 2 * Math.PI, !0), x.closePath(), x.fill())),
					0
				}
			}
			function drawBestHighLowValue(t, n, e, r, i) {
				var o, u, l, a, s;
				return s = he(n, e, r),
				u = s[0],
				l = s[1],
				t.fillStyle = Ie["Main Text"],
				t.font = "11px Consolas, Monospace",
				t.textBaseline = "middle",
				o = n[D],
				u < o.x + o.w / 2 ? (a = "← " + r.toFixed(2), t.textAlign = "left", u += 3) : (a = r.toFixed(2) + " →", u -= 3, t.textAlign = "right"),
				t.fillText(a, u + i, l)
			}
			function Pi(t, n, e) {
				return t.beginPath(),
				t.moveTo(n, e),
				t.lineTo(n + 6, e + 3),
				t.lineTo(n + 6, e - 3),
				t.fill()
			}
			function showLoading() {
//				return vn++,
//				$(".inner .text", loadingDiv).text("Loading..."),
//				vn ? loadingDiv.fadeIn("fast") : void 0
			}
			function showLoadingError() {
	//	return $(".inner", loadingDiv).html('<div class="text" style="font-family:微软雅黑;">加载数据失败，5秒后尝试重新加载...</div>')
			}
			function showInitLoading() {
				//return $(".inner", loadingDiv).html("<div class=text>数据加载中...</div>")
			}
			function hideLoading() {
//				return vn--,
//				vn ? void 0 : loadingDiv.fadeOut()
			}
			function showNotify(t) {
				return $("#notify .inner").text(t),
				$("#notify").fadeIn("fast").delay(800).fadeOut()
			}
			function Li(t, n) {
				var e, r, i, o, u, l, a;
				for (null == n && (n = !1), o = null, r = 0, l = 0, a = t.length; a > l; l++) if (e = t[l], e.price_currency === Gn) {
					if (e.tid = parseInt(e.tid), o = e.tid, ir[e.tid] || e.tid <= gn) continue;
					u = ae(e);
					for (interval in historicalData) me.call(historicalData, interval) && (i = historicalData[interval], ee(i, u));
					for (n && un(A, e.price), ir[o] = u, cr.push(u), mi.push(u); mi.length > 200;) mi.shift(); ++r
				}
				return [o, r]
			}
			function Wi(t) {
				var n, e, r, i, o, u;
				return e = function() {
					var e, r, i, o;
					for (i = t.reverse(), o = [], e = 0, r = i.length; r > e; e++) n = i[e],
					n.price = n.price,
					n.amount = n.amount,
					n.price_currency = Gn,
					o.push(n);
					return o
				} (),
				i = {
					result: "success",
					"return": e
				},
				"success" !== i.result && logPrint("failed, " + i.error),
				e = i["return"],
				0 !== e.length ? (u = Li(e), o = u[0], r = u[1]) : void 0
			}
			function Ui(t, n) {
				var e, r, i, o, u;
				return u = Re[t],
				(null != u ? u.tid: void 0) > n.tid || (i = $("#market_" + t), 0 === i.length) ? void 0 : (o = parseFloat(i.text()), e = parseFloat(n.last), r = -1 !== t.indexOf("cny") ? "<span class=eprice>" + (e / $c_usdcny).toFixed(2) + "/</span>" + e.toString() : e.toString(), i.html(r), Re[t] = n)
			}
			function Gi(t) {
				var n, e, r, i, o, u, l, a, s, c, f, h, d, p, g;
				if (a = t, null != a ? a["return"] : void 0) {
					for (logPrint.d("apply sdepth"), p = a["return"], n = p.asks, e = p.bids, u = p.now, n.length < q ? un(A, 1 / 0) : n.length && (r = n[n.length - 1][0], un(A, r)), e.length < q ? un(A, -1 / 0) : e.length && (i = e[0][0], un(A, i)), l = [["ask", n], ["bid", e]], c = 0, h = l.length; h > c; c++) for (g = l[c], o = g[0], s = g[1], f = 0, d = s.length; d > f; f++) yi = s[f],
					yi[2] = o,
					pn(A, yi, !1);
					return updateTimePassed(),
					X = u,
					Or = !0
				}
			}
			function Hi(t) {
				var n, e, r, i, o, u, l, a, s, c, f, h, d, p, g, m, v, y, _, x;
				if (c = t, null != c ? c["return"] : void 0) {
					for (_ = c["return"], n = _.asks, e = _.bids, f = _.time, l = _.now, r = JSON.stringify(c["return"]), s !== r && (updateTimePassed(), s = r), a = [["ask", n], ["bid", e]], an(A), d = 0, m = a.length; m > d; d++) for (x = a[d], u = x[0], h = x[1], p = 0, v = h.length; v > p; p++) yi = h[p],
					yi[2] = u,
					pn(A, yi);
					for (o = 0, i = parseInt(f), In = i; (yi = B[0]) && parseInt(yi[3]) < i;)++o,
					B.shift();
					for (logPrint.d("remove " + o + " depth"), g = 0, y = B.length; y > g; g++) yi = B[g],
					pn(A, yi);
					return logPrint.d("apply " + B.length + " depth"),
					logPrint.d("load " + n.length + " asks and " + e.length + " bids"),
					X = l,
					Or = !0
				}
			}
			function qi(t, n) {
				function e(e) {
					function o() {
						showInitLoading(),
						Be = t,
						storeChartInfoIntoCookie("step", Be, {
							mode: "session"
						}),
						ie = i,
						Tr = null,
						br = null,
						drawChart(),
						n(null)
					}
					return e ? (showLoadingError(), logPrint("switch failed " + r.message), n(r, i)) : (o(), void 0)
				}
				var r, i;
				zi(t,
				function() {
					e((r = arguments[0], i = arguments[1], r))
				})
			}
			function zi(t, e) {
				function r() {
					return e(null, u)
				}
				var i, o, u, l;
				logPrint("switch to " + intervals[t]);
				if (historicalData[t] && !historicalData[t].is_simple) ue(16,
				function() {
					u = historicalData[t],
					ye = t,
					ce = fe[t],
					r()
				});
				else {
					logPrint("get history data from server for " + intervals[t]);
					params = {
						market: $market,
						interval: interval,
						last: 0
					};
					showLoading();   
					
 
				//	$host + "chart_data"
					//On("/chart_data", params,
					On("/Line/get-"+market+"-"+interval+"?jsoncallback=?", "",
					function() {
						return o = arguments[0], 
						i = arguments[1],
						hideLoading(),
						o ? e(o) : i ? (ye = t, ce = fe[t] = i, Yi(), u = historicalData[t], u.is_simple = true, r(), void 0) : e(new Error("error, history data is empty"))
					})
				}
			}
			function Yi() {
				var t, n, e, r, i, o, u, l;
				for (interval = ye, r = ce, i = re(interval, r), n = t = i[f], i[Ke] = Yn(Y(t, v)), logPrint("apply " + cr.length + " txes"), u = 0, l = cr.length; l > u; u++) o = cr[u],
				o[S] > i[Ke] && ee(i, o);
				return i[nr] = function() {
					var n, r, i, o;
					for (i = on.price_mas.params, o = [], n = 0, r = i.length; r > n; n++) e = i[n],
					o.push(L(t, h, e));
					return o
				} (),
				i[tr] = function() {
					var n, r, i, o;
					for (i = on.price_mas.params, o = [], n = 0, r = i.length; r > n; n++) e = i[n],
					o.push(N(t, h, e));
					return o
				} (),
				i[rr] = function() {
					var n, r, i, o;
					for (i = on.volume_mas.params, o = [], n = 0, r = i.length; r > n; n++) e = i[n],
					o.push(L(t, b, e));
					return o
				} (),
				i[Ve] = W.apply(null, [t, h].concat(ve.call(on.macd.params))),
				i[er] = G.apply(null, [t, h].concat(ve.call(on.stoch_rsi.params))),
				i[Xe] = E.apply(null, [t, [m, y, h]].concat(ve.call(on.kdj.params))),
				i[Ze] = U(t, [_, m, y, h]),
				ie = historicalData[interval] = i,
				Er = true
			}
			function updateLastTimeByTrades(t) {
				return gn = t
			}
			function Ji(t) {
				return t.toString().replace(/\.\d+/, "<g>$&</g>")
			}
			function Xi(t, e) {
				var r, i, o, u, l, a, s;
				return null == e && (e = "green"),
				r = "green" === e ? "<i class=fa-arrow-up>": "<i class=fa-arrow-down>",
				n ? (t[k] < 1e-8 ? (a = ["0", null], l = a[0], u = a[1]) : (s = parseFloat(t[k].toPrecision(7)).toString().substr(0, 7).split("."), l = s[0], u = s[1]), u = null != u ? "." + u: "") : (l = "", u = "?"),
				o = document.createElement("div"),
				o.setAttribute("class", "row"),
				i = zn(new Date(1e3 * t[C])),
				o.innerHTML = "<div class=v>" + l + "<g>" + u + "</g></div><div class=t>" + i + '</div><div class="p ' + e + '">' + parseFloat(t[M].toPrecision(8).substr(0, 8)) + "</div>",
				o.tx = t,
				o.tx_style = e,
				o
			}
			function drawTradeChartAndData() {
				var t, n, e, r, i, o, u, l, a, s, c, f;
				for (mi.sort(function(t, n) {
					return t[S] - n[S]
				}), e = xr, r = xr.childNodes.length, u = 0, a = mi.length; a > u; u++) {
					for (o = mi[u], c = e.childNodes, l = 0, s = c.length; s > l && (t = c[l], !(t.tx[S] <= o[S])); l++);
					i = "bid" === o[F] ? "green": "ask" === o[F] ? "red": t ? t.tx[M] < o[M] ? "green": t.tx[M] > o[M] ? "red": t.tx_style: "green",
					null == o.count && (o.count = 1),
					n = Xi(o, i),
					(null != t ? t.tx[S] : void 0) < o[S] ? o[C] - t.tx[C] <= 1 && t.tx[F] === o[F] ? (o[k] += t.tx[k], o.count += t.tx.count, n = Xi(o, i), t.innerHTML = n.innerHTML, t.tx = n.tx) : (e.insertBefore(n, t), r && !$is_mobile &&
					function(t) {
						var n;
						return n = $(t),
						n.addClass("new"),
						n.hide(),
						n.slideDown(function() {
							return setTimeout(function() {
								return n.removeClass("new")
							},
							960)
						})
					} (n)) : e.appendChild(n)
				}
				for (; e.childNodes.length > 200;) e.removeChild(e.childNodes[e.childNodes.length - 1]);
				return (hn = null != (f = e.childNodes[0]) ? f.tx: void 0) && (i = e.childNodes[0].tx_style, ni.text(hn[M].toString()).attr("class", i), nn = !1),
				mi = [],
				Er = true,
				this
			}
			function updateDepth() {
				return;
				var t, n, e, r, a, s, c, f, h, d, p, g, m, v;
				for (c = A, n = c[i], r = c[u], n = n.slice(0, q - 1), r = r.slice( - q, -1), no("asks", n), no("bids", r), a = c[o].slice(0, q - 1), h = [], f = 0, p = 0, m = a.length; m > p; p++) t = a[p],
				f += t[1],
				h.push([t[0], f]);
				for (no("gasks", h), s = c[l].slice( - q, -1), d = [], f = 0, s.reverse(), g = 0, v = s.length; v > g; g++) e = s[g],
				f += e[1],
				d.push([e[0], f]);
				return d.reverse(),
				no("gbids", d),
				h.length && (Dn = h[h.length - 1][1]),
				d.length && (Pn = d[0][1]),
				null
			}
			function updateTimePassed() {}
			function Zi(t) {
				var n;
				return n = t.toPrecision(9).substr(0, 9).replace(/(.[^.])(0+)$/, "$1<g>$2</g>")
			}
			function to(t) {
				var n;
				return n = t.toPrecision(6).substr(0, 6).replace(/(.[^.])(0+)$/, "$1<g>$2</g>")
			}
			function no(t, n) {
				var e, r, i, o, u, l, a, s, c, f, h, d, p, g, m, v, y, _, x, w, b, k;
				u = "g" === t[0],
				o = -1 !== t.indexOf("ask"),
				null == te[t] && (te[t] = {}),
				p = te[t],
				h = $("#" + t + " .table"),
				r = 1200,
				l = Date.now(),
				a = [];
				for (s in p) me.call(p, s) && (g = p[s], a.push(parseFloat(s)));
				for (n.reverse(), a.sort(function(t, n) {
					return n - t
				}), m = -1, v = function(t, n) {
					var e, i, o, s, c, f, d;
					if (u ? (s = (t * Ce).toFixed(Se), e = Math.round(n)) : (s = t.toPrecision(12), e = n.toPrecision(6).substr(0, 6)), t = parseFloat(s), n = parseFloat(e), p[t]) c = p[t],
					u || n === c.amount || (n > c.amount ? c.ob_amount.css("color", "#6C6") : n < c.amount && c.ob_amount.css("color", "#F66"), setTimeout(function() {
						return c.ob_amount.css("color", "inherit")
					},
					r));
					else {
						for (c = $("<div class=row><span class=price></span> <span class=amount></span></div>"), i = !1, f = 0, d = a.length; d > f; f++) if (o = a[f], t > o) {
							p[o].before(c),
							i = !0;
							break
						}
						i || h.append(c),
						a.length && H && (c.addClass("new"), $is_mobile ? setTimeout(function() {
							return c.removeClass("new")
						},
						1.2 * r) : (c.hide(), c.slideDown(function() {
							return setTimeout(function() {
								return c.removeClass("new")
							},
							.8 * r)
						}))),
						p[t] = c,
						c.ob_price = $(".price", c),
						c.ob_amount = $(".amount", c)
					}
					return u || (s = Zi(t), e = Ji(to(n))),
					m === parseInt(t) && (s = s.replace(/(\d+)\./, "<h>$&</h>")),
					c.amount_str !== e && (c.ob_amount.html(e), c.amount_str = e),
					c.price_str !== s && (c.ob_price.html(s), c.price_str = s),
					m = parseInt(t),
					c.price = t,
					c.amount = n,
					c.found_at = l
				},
				i = _ = 0, w = n.length; w > _; i = ++_) k = n[i],
				s = k[0],
				e = k[1],
				v(s, e);
				i = 0,
				c = [];
				for (s in p) me.call(p, s) && (d = p[s], c.push(parseFloat(s)));
				for (c.sort(function(t, n) {
					return n - t
				}), o && c.reverse(), i = 0, f = 0, y = function(t, n) {
					return i > q + f && (n.remove(), delete p[t]),
					n.found_at < l && q > i ? (f++, n.addClass("remove"), n.removeClass("new"), delete p[t], $is_mobile ? setTimeout(function() {
						return n.remove()
					},
					1.2 * r) : setTimeout(function() {
						return n.slideUp(function() {
							return n.remove()
						})
					},
					r)) : ++i
				},
				x = 0, b = c.length; b > x; x++) s = c[x],
				d = p[s],
				y(s, d);
				return n.reverse(),
				Er = true,
				this
			}
			function eo() {
				function t(t) {
					return t ? (logPrint("retry after 5 seconds"), ue(5e3,
					function(t) {
						eo(t)
					}), void 0) : (periodOptions[interval].addClass("selected"), periodObject = periodOptions[interval], updateTrades())
				}
				var n;
				interval = parseInt(null != (n = $.cookie("step")) ? n: 900),
				qi(interval,
				function() {
					t((Mr = arguments[0], ai = arguments[1], Mr))
				})
			}
			function updateTrades() {
				hideLoading();
//				function t(t) {
//					var n, e, r = this;
//					logPrint("get history trades"),
//					On($host + "chart_trades", {
//						last: 0,
//						market: $market
//					},
//					function() {
//						var i, o, u;
//						if (Mr = arguments[0], ai = arguments[1], Mr) return logPrint(Mr),
//						t();
//						updateTradeFromChart(ai);
//						for (u = ai.reverse(), i = 0, o = u.length; o > i; i++) n = u[i],
//						n.tid <= gn && (e = le(n), mi.push(e));
//						Nr = !0,
//						t(r)
//					})
//				}
//				return updateLastTimeByTrades(Yn(Y(ie[f], v))),
//				logPrint("Initialize Depth Digger"),
//				ln = !0,
//				$test ? void 0 : (logPrint("Initialize FullSync System"), gi = !1, (vi = function(n) {
//					var e, r, i, o, u;
//					n = jn("FullSync"),
//					t(function() {
//						function t() {
//							e = "",
//							On($host + "chart_trades", {
//								last: gn ? gn: 0,
//								market: $market
//							},
//							function() {
//								function l() {
//									function l() {
//										function l() {
//											var l;
//											if (l = Li(r), o = l[0], i = l[1], i > 0 && (ke ? n("found " + i + " missed trade" + (i > 1 ? "s": "") + e) : n("found " + i + " trade" + (i > 1 ? "s": "") + e)), o) {
//												for (updateLastTimeByTrades(o); (u = cr[0]) && u[T] < Date.now() - 3e4;) {
//													delete ir[u[S]],
//													cr.shift()
//												}
//												Nr = !0;
//												drawChart()
//											}
//											ue(refreshInterval,
//											function(n) {
//												t(n)
//											})
//										}
//										var a;
//										r = function() {
//											var t, n, e, r;
//											for (e = ai.reverse(), r = [], t = 0, n = e.length; n > t; t++) a = e[t],
//											a.price = a.price,
//											a.amount = a.amount,
//											a.price_currency = Gn,
//											r.push(a);
//											return r
//										} (),
//										ai = {
//											result: "success",
//											"return": r
//										},
//										"success" !== ai.result && n("failed, " + ai.error),
//										r = ai["return"],
//										0 === r.length ? ue(refreshInterval,
//										function() {
//											return t()
//										}) : l()
//									} (null != ai ? ai.reverse: void 0) ? l() : ue(refreshInterval,
//									function() {
//										return t()
//									})
//								}
//								Mr = arguments[0],
//								ai = arguments[1],
//								updateTradeFromChart(ai),
//								Mr ? ue(refreshInterval,
//								function() {
//									return t()
//								}) : l()
//							})
//						}
//						t()
//					})
//				})(logPrint),
//				function() {
//					function t() {
//						function o() {
//							function t() { (r = i.shift()) ? (Wi([r]), dn(A, r), Or = !0, ue(40 + 40 * Math.random(),
//								function(n) {
//									t(n)
//								})) : l()
//							}
//							function l() {
//								a(0)
//							}
//							function a() {
//								o(0)
//							}
//							for (; tn.length > 5;) tn.shift();
//							if (e = tn.shift(), !e) return u();
//							switch (e.type) {
//							case "trades":
//								for (i = e.trades.reverse(); i.length > 20;) r = i.shift(),
//								Wi([r]),
//								dn(A, r);
//								t();
//								break;
//							case "sdepth":
//								n = e.sdepth,
//								a(Gi(e.sdepth));
//								break;
//							case "depth":
//								Hi(e.depth),
//								n && Gi(n),
//								a(0)
//							}
//						}
//						function u() {
//							ue(100,
//							function(n) {
//								t(n)
//							})
//						}
//						o()
//					}
//					var n, e, r, i;
//					n = null,
//					t()
//				} (),
//				function() {
//					function t() {
//						ue(1e3,
//						function() {
//							ke ? Te < Date.now() - 1e4 && cn ? t($("#realtime_error").fadeIn()) : t($("#realtime_error").fadeOut()) : t()
//						})
//					}
//					t()
//				} (),
//				function() {
//					function t() {
//						ue(1e3,
//						function() {
//							n = new Date,
//							t(e.text(Ln(n)))
//						})
//					}
//					var n, e;
//					e = $("#now"),
//					t()
//				} (), Wr.click(function() {
//					try {
//						logPrint("------"),
//						cr.length && logPrint("cached txes length: " + cr.length + ", first is " + zn(new Date(1e3 * cr[0][C]))),
//						logPrint("sorted txes length: " + fi.length),
//						logPrint("last tid: " + gn + " " + zn(new Date(gn / 1e3))),
//						B.length && logPrint("depth cache length " + B.length + ", first is " + zn(new Date(parseInt(B[0].now) / 1e3))),
//						logPrint("depth ask length " + A[i].size()),
//						logPrint("depth bid length " + A[u].size()),
//						logPrint("realtime active at " + zn(new Date(Te))),
//						logPrint("-- STATUS --")
//					} catch(t) {
//						Mr = t,
//						logPrint(Mr.message)
//					}
//					return ! 0
//				}), qi(Be,
//				function() {
//					return hideLoading()
//				}),
//				function() {
//					function t(t) {
//						return t >= 0 ? "+" + t.toFixed(2) + "%": t.toFixed(2) + "%"
//					}
//					function n() {
//						var n, e, l, a, s, c, f, h, d, p, g, m, v, y, _, x, w, b, k, T, C;
//						for (l = [["buy", i], ["sell", u]], x = 0, b = l.length; b > x; x++) {
//							for (T = l[x], _ = T[0], e = T[1], d = A[e].flatten(), e === u && d.reverse(), m = parseFloat(o.val()), g = m, y = 0, v = 0, f = $("." + _ + "_reach", r), a = $("." + _ + "_avg", r), h = $("." + _ + "_reach_p", r), s = $("." + _ + "_avg_p", r), c = $("." + _ + "_cost", r), w = 0, k = d.length; k > w; w++) {
//								if (C = d[w], p = C[0], n = C[1], !(g > n)) {
//									y += p * g,
//									v += g;
//									break
//								}
//								y += p * n,
//								v += n,
//								g -= n
//							}
//							hn && p && !isNaN(m) && Math.abs(v - m) < 1e-6 ? (f.text(p), h.text(t(100 * (p / hn[M]) - 100)), a.text(parseFloat((y / v).toPrecision(6))), s.text(t(100 * (y / v / hn[M]) - 100)), c.text(parseFloat(y.toPrecision(6)))) : (f.text("out of orderbook"), a.text("out of orderbook"), c.text(""), h.text(""), s.text(""))
//						}
//						return ! 0
//					}
//					var r, o, l;
//					return r = $("#dlg_estimate_trading"),
//					o = $(".amount", r),
//					o.keyup(n),
//					o.val(10),
//					(l = function() {
//						return n(),
//						setTimeout(l, 1e3)
//					})(),
//					0
//				} ())
			}
			var io, oo, uo, lo, ao, so, co, fo, ho;
			if (lo = s(6), Ye = lo[0], Je = lo[1], ze = lo[2], je = lo[3], He = lo[4], qe = lo[5], window.$script_loaded = !0, De = window.$them_dark, Pe = window.$theme_light, "dark" === $theme_name ? (Ie = $theme_dark, $("html").attr("class", "dark")) : (Ie = $theme_light, $("html").attr("class", "light")), $.support.cors = !0, windowObj = $(window), Qr = $("#main"), rightBlock = $("#sidebar_outer"), Jr = $("#header_outer"), jr = $("#footer_outer"), rightBannerBlock = $("#leftbar_outer"), Zr = $("#nav"), oi = $("#wrapper"), qr = $("#date"), Wr = $("#assist"), ti = $("#periods"), ri = $("#trades"), zr = $("#depth"), Ur = $("#before_trades"), Lr = $("#ask"), Gr = $("#bid"), ni = $("#price"), Vr = $("#markets"), Hr = $("#chart_info"), Yr = {
				asks: $("#asks div"),
				bids: $("#bids div"),
				gasks: $("#gasks div"),
				gbids: $("#gbids div")
			},
			xr = ri[0], mainCanvas = $("#canvas_main")[0], crossCanvas = $("#canvas_cross")[0], !mainCanvas.getContext) return Qr.html("<div style=\"margin:6px\">\n<div>Sorry, your browser doesn't support chart.</div>\n<dl>\n	<dt>Minimum requirements:</dt>\n	<dd>IE 9+, Chrome, Firefox or other modern browser.</dd>\n	<dt>Recommaned requirements:</dt>\n	<dd>IE 10+, Chrome, Firefox or other modern browser.</dd>\n	<dt>Remarks:</dt>\n</div>"),
			void 0;
			pr = mainCanvas.getContext("2d"),
			dr = crossCanvas.getContext("2d"),
			bannerBlockWidth = rightBannerBlock.width(),
			function() {
				function t(t) {
					function n(n, e) {
						var r, i, o, u;
						if (null == e && (e = ""), $debug) {
							if (e && (e = ' class_name="' + e + '"'), i = zn(new Date), Wr.prepend($("<div" + e + "/>").html("[" + i + ("] " + t + ": ") + n)), o = Wr[0], r = o.childNodes, u = r.length, u > 100) for (; u-->50;) o.removeChild(r[u]);
							return this
						}
					}
					return n.d = function() {
						return $debug ? n.apply(null, arguments) : void 0
					},
					n
				}
				return jn = t
			} (),
			logPrint = jn("Eva"), 
			logPrint("Welcome to vip.com"),
			intervals = {
				60 : "1" + lang.mn,
				300 : "5" + lang.mn,
				600 : "10" + lang.mn,
				900 : "15" + lang.mn,
				1800 : "30" + lang.mn,
				3600 : "1" + lang.hr,
				7200 : "2" + lang.hr,
				14400 : "4" + lang.hr,
				21600 : "6" + lang.hr,
				28800 : "8" + lang.hr,
				86400 : "1" + lang.dt
			},
			pi = {};
			for (Fr in intervals) me.call(intervals, Fr) && (yi = intervals[Fr], pi[yi] = Fr);
			for (periodOptions = {},
			periodObject = null, ao = $("li.period", ti), oo = 0, uo = ao.length; uo > oo; oo++) Br = ao[oo],
			Br = $(Br),
			(interval = pi[Br.text()]) && (periodOptions[interval] = Br,
			function(t, n) {
				return n.click(function() {
					var e, r;
					interval = parseInt(t);
					qi(t,
					function() {
						return e = arguments[0],
						r = arguments[1],
						e ? void 0 : (periodObject && periodObject.removeClass("selected"), periodObject = n, periodOptions[t].addClass("selected"), !0)
					})
				})
			} (interval, Br));
			ir = {},
			cr = [],
			fi = [],
			mi = [],
			ke = null != window.WebSocket,
			Te = Date.now(),
			en = !1,
			so = s(10),
			Ke = so[0],
			nr = so[1],
			tr = so[2],
			rr = so[3],
			Ve = so[4],
			Ge = so[5],
			Qe = so[6],
			er = so[7],
			Xe = so[8],
			Ze = so[9],
			historicalData = {},
			ie = null,
			A = fn(),
			B = [],
			J = null,
			z = null,
			X = 0,
			ce = null,
			ye = null,
			fe = {},
			Zn = !1,
			Be = 60,
			Dn = 0,
			Pn = 0,
			In = 0,
			Le = 1e3,
			Ne = 1,
			Ee = 3e4,
			refreshInterval = 10000,
			Ue = Date.now(),
			q = 15,
			O = 15,
			H = !0,
			tn = [],
			$(window).on("mousemove",
			function() {
				return Le = 1e3,
				Ue = Date.now()
			}),
			function() {
				function t() {
					Ne = .1 + (Date.now() - Ue) / 1e3 / 10 / 60,
					ue(500,
					function(n) {
						t(n)
					})
				}
				t()
			} (),
			on = {
				price_mas: {
					cookie: "price_ma_cycles",
					params: [7, 30, 100, 200],
					names: ["MA%", "MA%", "MA%", "MA%"]
				},
				volume_mas: {
					cookie: "volume_ma_cycles",
					params: [5, 10, 20],
					names: ["MA%", "MA%", "MA%"]
				},
				macd: {
					cookie: "macd_params",
					params: [12, 26, 9],
					names: ["DIF", "DEA", "MACD"]
				},
				stoch_rsi: {
					cookie: "stock_rsi_params",
					params: [14, 14, 3, 3],
					names: ["K", "D"]
				},
				kdj: {
					cookie: "kdj_params",
					params: [9, 3, 3],
					names: ["K", "D", "J"]
				}
			},
			hn = null,
			gn = null,
			Tn = null,
			$n = null,
			Oe = null,
			t = {},
			Fe = !1,
			window.$is_mobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent),
			V = {
				depth_hint: !0,
				sidebar: !0
			},
			Me = .5,
			Se = 1,
			A = fn(Me),
			Q = null,
			fr = null,
			hr = null,
			or = 100,
			ne = 8,
			ur = parseInt(null != (fo = $.cookie("barWidth")) ? fo: 5),
			Cr = parseInt(null != (ho = $.cookie("gapWidth")) ? ho: 3),
			$i = (ur - 1) / 2,
			vr = 0,
			sr = ur + Cr,
			Ir = null,
			lr = null,
			ar = null,
			ui = null,
			li = null,
			gr = null,
			mr = null,
			si = null,
			ci = null,
			$r = false,
			Er = false,
			Nr = false,
			Or = false,
			Tr = null,
			br = null,
			$e = 0,
			wr = null,
			kr = null,
			a = false,
			r = null,
			function() {
				function t(t) {
					var n, e, r, i;
					return r = t.originalEvent,
					r && 1 === (null != (i = r.touches) ? i.length: void 0) && (t = r.touches[0]),
					n = t.pageX - bannerBlockWidth,
					e = t.pageY - Jr.height(),
					si = n,
					ci = e,
					kr = Math.floor((n - vr) / sr),
					n = kr * sr + $i + vr,
					gr = n,
					mr = e
				}
				var n;
				return oi.dblclick(function() {
					return a ? Mi() : Ti(),
					!0
				}),
				oi.bind("mousemove",
				function(n) {
					return a || (t(n), drawTickInfo(), $r && drawMainChart()),
					!0
				}),
				oi.mouseout(function() {
					return a || (gr = null, si = null, Si()),
					!0
				}),
				n = !1,
				windowObj.bind("mouseup",
				function() {
					return n ? void 0 : (n = !0, setTimeout(function() {
						return n = !1
					},
					100), $r = !1, !0)
				}),
				oi.bind("mousedown",
				function(n) {
					return $r = !0,
					t(n),
					wr = Tr + kr,
					!1
				})
			} (),
			window.world_draw_main = drawMainChart = function() {
				function e(t) {
					return null == t && (t = ne),
					p.y = p.y + p.h - t - 1,
					W.push(p.y),
					kn.strokeStyle = Ie.Border,
					bn(kn, p.y + .5, 0, hr),
					p.y -= t
				}
				var r, o, l, a, s, c, p, g, v, x, $, w, k, T, C, S, F, B, O, N, E, L, W, U, G, H, q, z, j, J, X, K, V, tn, nn, en, rn, on, un, ln, an, sn, cn, fn, dn, pn, gn, periodOptions, vn, kn, In, Rn, On, En, Ln, Gn, zn, jn, Jn, Kn, Zn, te, ee, re, oe, ue, le, ae, be, ce, fe, me, ve, ye, _e, xe, we, be, ke, Te, Me, Ce, Se, Ae, De, Pe, Re, Ne, Ee, Le, refreshInterval, Ue, Ge, Ke, Qe, ir, cr, dr, gr, mr, yr, xr, $r, wr, kr, Mr, Cr, Sr, Ar, Br, logPrint, Rr, Or, Nr, Er, Lr, Wr, Ur;
				if (ie) {
					for (l = ie[f], r = $settings.stick_style.value, kn = pr, mainCanvas.width = mainCanvas.width, F = Y(l, h).length - 1, F > $e && Tr && $e === Tr + lr && (Tr += F - $e, $e = F), Tr > F && (Tr = F), 0 > Tr && (Tr = 0), br = Tr + lr, br > F && (br = F), "mas" === $settings.main_lines.value ? (Gn = ie[nr], be = ie[rr]) : "emas" === $settings.main_lines.value ? (Gn = ie[tr], be = ie[rr]) : (Gn = [], be = []), be = [], "macd" === $settings.indicator.value ? (an = ie[Ve], Br = R(Z, l, Tr, br, an), T = Br[0], k = Br[1], on = Br[2], j = [T, k], N = Xn([T, k, on]), J = -N, z = 2 * N) : "stoch_rsi" === $settings.indicator.value ? (Kn = ie[er], logPrint = R(Z, l, Tr, br, Kn), jn = logPrint[0], zn = logPrint[1], j = [jn, zn], J = 0, z = 100) : "kdj" === $settings.indicator.value && (K = ie[Xe], Rr = R(Z, l, Tr, br, K), Fr = Rr[0], $ = Rr[1], X = Rr[2], j = [Fr, $, X], fn = Qn([Fr, $, X, [100]]), gn = Vn([Fr, $, X, [0]]), J = gn, z = fn - gn), Or = R(Z, l, Tr, br, [b, _, h, m, y, d]), fe = Or[0], Rn = Or[1], c = Or[2], L = Or[3], en = Or[4], w = Or[5], ae = R(Z, l, Tr, br, be), Ln = R(Z, l, Tr, br, Gn), O = Math.floor((hr - or) / sr), H = L.slice(0, O), q = en.slice(0, O), "line" !== r && "line_o" !== r || "m" !== $settings.line_style.value || (c = R(Z, l, Tr, br, ie[Ze])[0]), g = lr * sr, vr = hr - or - ar * sr, p = {
						x: vr,
						y: fr,
						w: g,
						h: fr
					},
					C = {
						x: 0,
						y: 0,
						w: lr,
						h: 0
					},
					W = [], p.h = -16, o = ge(p, C), e(0), p.y -= ne, p.h = -Ir, "none" === $settings.indicator.value ? ln = null: (C.y = J, C.h = z, ln = ge(p, C), e()), ae.length ? (C.y = 0, C.h = Qn([ae, fe])) : (C.y = 0, C.h = Qn([fe])), ce = ge(p, C), $n = ln, Oe = ce, e(), Ln.length ? (U = [Ln, L], rn = [Ln, en]) : (U = [L], rn = [en]), fn = 1.01 * Qn(U), gn = .99 * Vn(rn); fn && fn < L[L.length - 1];) fn *= 1.01;
					for (; gn && gn > en[en.length - 1];) gn *= .99;
					if (p.h = -p.y + ne + 12, C.y = gn, C.h = fn - gn, cn = ge(p, C, "logarithmic" === $settings.scale.value), Tn = cn, ln) if (oe = ln, "macd" === $settings.indicator.value) for (Zn = pe(oe, 0), En = on[0], S = ye = 0, we = on.length; we > ye; S = ++ye) B = on[S],
					B > 0 ? (kn.fillStyle = Ie["Green Fill"], kn.strokeStyle = Ie["Green Stroke"]) : (kn.fillStyle = Ie["Red Fill"], kn.strokeStyle = Ie["Red Stroke"]),
					ki(B, En) && (kn.fillStyle = Ie.Background),
					drawVolumeBar(kn, oe, Zn, S, B, ur),
					En = B;
					else if ("stoch_rsi" === (Qe = $settings.indicator.value) || "kdj" === Qe) for (ir = [20, 80], _e = 0, Me = ir.length; Me > _e; _e++) yi = ir[_e],
					Zn = pe(oe, yi),
					bn(kn, Zn + .5, 0, hr);
					for (Zn = pe(ce, 0), On = c[0], S = xe = 0, Ce = c.length; Ce > xe; S = ++xe) {
						switch (B = c[S], r) {
						case "candle_stick_hlc":
							In = null != (cr = c[S - 1]) ? cr: Rn[S],
							s = c[S];
							break;
						default:
							In = Rn[S],
							s = c[S]
						}
						if (nn = en[S], E = L[S], s > In ? (kn.fillStyle = Ie["Green Fill"], kn.strokeStyle = Ie["Green Stroke"]) : (kn.fillStyle = Ie["Red Fill"], kn.strokeStyle = Ie["Red Stroke"]), ki(s, In) && (kn.fillStyle = Ie.Background), drawVolumeBar(kn, ce, Zn, S, fe[S], ur), "ohlc" === r || "candle_stick" === r || "candle_stick_hlc" === r) switch (drawHighLowLine(kn, cn, S, nn, E, $i), r) {
						case "ohlc":
							v = de(cn, S),
							x = pe(cn, In),
							bn(kn, x + .5, v, v + $i),
							x = pe(cn, s),
							bn(kn, x + .5, v + $i, v + ur);
							break;
						case "candle_stick":
							drawOpenCloseBar(kn, cn, S, In, s, ur);
							break;
						case "candle_stick_hlc":
							drawOpenCloseBar(kn, cn, S, In, s, ur)
						}
						On = B
					}
					if ("line" === r || "line_o" === r) {
						for (kn.beginPath(), kn.fillStyle = Ie["Green Area"], dr = he(cn, [0, L[0]]), v = dr[0], x = dr[1], kn.moveTo(v + $i, x), S = Ee = 0, Se = L.length; Se > Ee; S = ++Ee) B = L[S],
						gr = he(cn, S, B),
						v = gr[0],
						x = gr[1],
						kn.lineTo(v + $i, x);
						for (S = Le = mr = c.length - 1; 0 >= mr ? 0 >= Le: Le >= 0; S = 0 >= mr ? ++Le: --Le) B = c[S],
						yr = he(cn, S, B),
						v = yr[0],
						x = yr[1],
						kn.lineTo(v + $i, x);
						for (kn.fill(), kn.beginPath(), kn.fillStyle = Ie["Red Area"], xr = he(cn, [0, en[0]]), v = xr[0], x = xr[1], kn.moveTo(v + $i, x), S = refreshInterval = 0, Ae = en.length; Ae > refreshInterval; S = ++refreshInterval) B = en[S],
						$r = he(cn, S, B),
						v = $r[0],
						x = $r[1],
						kn.lineTo(v + $i, x);
						for (S = Ue = wr = c.length - 1; 0 >= wr ? 0 >= Ue: Ue >= 0; S = 0 >= wr ? ++Ue: --Ue) B = c[S],
						kr = he(cn, S, B),
						v = kr[0],
						x = kr[1],
						kn.lineTo(v + $i, x);
						if (kn.fill(), kn.lineWidth = 2, kn.strokeStyle = Ie["Stick Line"], Sn(kn, cn, c, $i + .5), "line_o" === r) for (kn.fillStyle = Ie.Background, kn.strokeStyle = Ie["Stick Line"], S = Ge = 0, De = c.length; De > Ge; S = ++Ge) B = c[S],
						Mr = he(cn, S, B),
						v = Mr[0],
						x = Mr[1],
						kn.beginPath(),
						kn.arc(v + $i + .5, x, 2, 0, 2 * Math.PI, !0),
						kn.closePath(),
						kn.fill(),
						kn.stroke();
						kn.lineWidth = 1,
						L = c,
						en = c
					}
					for (kn.lineWidth = 1, a = [[cn, Ln, !0], [ce, ae, !0]], ln && a.unshift([ln, j, !0]), Ke = 0, Pe = a.length; Pe > Ke; Ke++) if (Cr = a[Ke], oe = Cr[0], ve = Cr[1], Jn = Cr[2], Jn) for (G = Nr = 0, Re = ve.length; Re > Nr; G = ++Nr) me = ve[G],
					kn.strokeStyle = Ie.Colors[G],
					Sn(kn, oe, me, $i + .5);
					for (kn.lineWidth = 1, dn = 0, pn = 0, G = Er = 0, Ne = H.length; Ne > Er; G = ++Er) yi = H[G],
					yi > dn && (dn = yi, pn = G);
					for (mn = 1 / 0, vn = 0, G = Lr = 0, be = q.length; be > Lr; G = ++Lr) yi = q[G],
					mn > yi && (mn = yi, vn = G);
					for (drawBestHighLowValue(kn, cn, pn, dn, $i), drawBestHighLowValue(kn, cn, vn, mn, $i),
					function() {
						function t(t, n) {
							var e;
							return e = 60 * t.getTimezoneOffset(),
							(t.getTime() / 1e3 - e) % n < Be
						}
						var n, e, r, i, u, a, s, c, f, h, g, m, y, _, x, $, b;
						if (interval = Be, r = null, i = null, n = null, e = null, c = {
							60 : {
								cond: t,
								key_cond: function(t) {
									return 0 === t.getMinutes()
								},
								text: function(t) {
									return qn(t)
								},
								key_text: function(t) {
									return Un(t)
								},
								over: function(t) {
									return Nn(t)
								}
							},
							3600 : {
								cond: t,
								key_cond: function(t) {
									return 0 === t.getHours() && t.getDate() !== n
								},
								text: function(t) {
									return Un(t)
								},
								key_text: function(t) {
									return n = t.getDate(),
									Nn(t)
								},
								over: function(t) {
									return Nn(t)
								}
							},
							86400 : {
								cond: t,
								key_cond: function(t) {
									return ! 1
								},
								text: function(t) {
									return Nn(t)
								},
								key_text: function(t) {
									return Nn(t)
								},
								over: function(t) {
									return t.getFullYear()
								}
							},
							604800 : {
								cond: function(t) {
									return t.getDate() < 8 && t.getMonth() !== r
								},
								key_cond: function(t) {
									return 0 === t.getMonth() && t.getFullYear() !== i
								},
								text: function(t) {
									return r = t.getMonth(),
									Hn(t)
								},
								key_text: function(t) {
									return i = t.getFullYear(),
									r = t.getMonth(),
									t.getFullYear()
								},
								over: function(t) {
									return t.getFullYear()
								}
							}
						},
						interval >= 86400) s = 604800,
						f = 604800;
						else for (f = interval * (80 / sr), 1800 >= f ? (s = 60, m = [10, 30]) : 28800 >= f ? (s = 3600, m = [1, 2, 3, 6, 8]) : 1296e3 >= f ? (s = 86400, m = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]) : (s = 604800, m = 1), $ = 0, _ = m.length; _ > $; $++) if (G = m[$], s * G > f) {
							f = s * G;
							break
						}
						if (p = o[D], a = c[s]) {
							for (kn.strokeStyle = Ie.Border, kn.textAlign = "center", kn.textBaseline = "middle", w = Y(l, d), g = b = x = Tr - 1; br >= x ? br >= b: b >= br; g = br >= x ? ++b: --b)(u = w[g]) && (G = g - Tr, a.cond(u, f) && (a.key_cond(u) ? (kn.fillStyle = Ie["Axis Text"], kn.font = "bold 12px Consolas, Monospace", y = a.key_text(u)) : (kn.fillStyle = Ie["Axis Text"], kn.font = "11px Consolas, Monospace", y = a.text(u)), v = vr + G * sr + $i + .5, Cn(kn, v, p.y + p.h, p.y + p.h + 4), kn.fillText(y, v, p.y + p.h + 8.5)));
							if (w[Tr]) return h = a.over(w[Tr]),
							qr.text(h)
						}
					} (), v = hr - or, kn.strokeStyle = Ie.Border, kn.textAlign = "left", kn.textBaseline = "middle", kn.font = "11px Consolas, Monospace", kn.fillStyle = Ie["Axis Background"], kn.fillRect(v, 0, v + or, fr), kn.fillStyle = Ie["Axis Text"], n = 1, ue = n ? [ln, cn, ce] : [ln, cn], Sr = function() {
						var t, n, e;
						for (n = [], e = 0, t = ue.length; t > e; e++) oe = ue[e],
						oe ? (oe = ge(oe[D], oe[P], oe[I]), oe[D].w = or, oe[D].x = v, n.push(oe)) : n.push(void 0);
						return n
					} (), un = Sr[0], sn = Sr[1], le = Sr[2], Wr = 0, ke = W.length; ke > Wr; Wr++) x = W[Wr],
					bn(kn, x + .5, v, v + or);
					for (yn(kn, sn, "hr"), ee = 0, re = 0, Fe = hn && 3600 > Be && Dn && Pn, Fe && (yi = ge(sn[D], sn[P], sn[I]), yi[D].x += 8, yi[D].w -= 8, yi[P].x = 0, yi[P].w = Q ? Q: Math.floor(Math.min(Dn, Pn) / 5), tn = yi[P].w, wn(kn, sn,
					function() {
						var n, e, r, o, l, a, s, c, f, h, d, g, m, y, _, $, w, b, k, T, M, C, S, F, B, P, I, R, O, N, E, L, W;
						for (kn.save(), kn.lineWidth = 2, t = {},
						interval = tn / yi[D].w, m = or, P = [[i, 0, 500, Ie["Green Arrow"], ze], [u, -1, -500, Ie["Red Arrow"], je]], N = 0, B = P.length; B > N; N++) {
							for (I = P[N], S = I[0], b = I[1], c = I[2], l = I[3], f = I[4], _ = t[S] = [], t[f] = [], G = E = 0; or >= 0 ? or >= E: E >= or; G = or >= 0 ? ++E: --E) _[G] = 0;
							for (a = [], T = 0, g = 0, kn.beginPath(), kn.fillStyle = l, kn.strokeStyle = l, d = yi[D].x, k = 0, w = 0, $ = 0, s = !1, G = L = b; (c >= b ? c >= L: L >= c) && (r = A[S].at(G)); G = c >= b ? ++L: --L) {
								if (F = r[1], y = r[0], T += F, R = he(yi, T, y), v = R[0], x = R[1], G === b && kn.moveTo(yi[D].x, x), kn.fillRect(d, x - 1, v - d + 1, 2), d = v, T > tn && (F = tn - (T - F), s = !0), re += F, ee += y * F, interval > w + F) _[k] += y * F,
								t[f][k] = y,
								w += F;
								else {
									for (_[k] += y * (interval - w), t[f][k] = y, k++, w = F + w - interval; w > interval;) w -= interval,
									_[k] += y * interval,
									t[f][k] = y,
									k++;
									_[k] += y * w,
									t[f][k] = y
								}
								if (s) break
							}
							m = Math.min(m, k)
						}
						for (M = 0, C = 0, kn.beginPath(), kn.lineWidth = 1.5, kn.strokeStyle = Ie["Minor Arrow"], t[Ye] = [], t[Je] = [], t[He] = [], t[qe] = [], n = 0, e = 0, p = yi[D], h = W = 0; (m >= 0 ? m > W: W > m) && (M += t[i][h] + t[u][h], n += t[i][h], e += t[u][h], !isNaN(M)); h = m >= 0 ? ++W: --W) C += interval,
						o = M / C / 2,
						t[Ye][h] = o,
						t[Je][h] = C,
						t[He][h] = n,
						t[qe][h] = e,
						O = he(yi, C, o),
						v = O[0],
						x = O[1],
						kn.lineTo(v, x);
						return kn.stroke(),
						kn.restore()
					})), ln && ("macd" === $settings.indicator.value ? _n(kn, un) : xn(kn, un, [0, 20, 50, 80, 100])), yn(kn, sn, "text"), n && yn(kn, le), Fe && (p = sn[D],
					function() {
						var t, n;
						t = hn[M],
						n = t,
						x = pe(cn, t),
						v = p.x,
						kn.strokeStyle = Ie["Arrow Text"],
						kn.fillStyle = Ie["Arrow Text"],
						Pi(kn, v, x),
						kn.fillStyle = Ie["Minor Arrow"],
						x = pe(cn, ee / re)
					} (), wn(kn, le,
					function() {
						var t, n;
						return kn.font = "11px Consolas, Monospace",
						t = Yn(Y(l, b)),
						n = he(ce, br - Tr + 1, t),
						v = n[0],
						x = n[1],
						Pr = kn.measureText(t.toFixed(5)),
						kn.fillStyle = Ie["Background Mask"],
						kn.fillRect(p.x + 12, x - 6, Pr.width, 12),
						kn.fillStyle = Ie["Highlight Text"],
						kn.fillText("←", p.x, x),
						kn.fillText(t.toFixed(5), p.x + 12, x)
					})), p = o[D], V = br - Tr, me = [[V, w[br]]], kn.font = "10px Arial, Sans", Ur = 0, Te = me.length; Te > Ur; Ur++) Ar = me[Ur],
					G = Ar[0],
					$ = Ar[1],
					$ && (0 === G && 120 / sr > V || (v = p.x + p.w, x = p.y + p.h + 8.5, kn.strokeStyle = Ie.Border, kn.fillStyle = Ie.Border, kn.strokeStyle = Ie["Axis Text"], kn.fillStyle = Ie["Axis Text"], kn.beginPath(), kn.arc(v, x, 2, 0, 2 * Math.PI, !0), kn.closePath(), kn.fillStyle = Ie["Axis Text"], te = Tr + G === F ? lang.now: updateLastBarTimePassed(parseInt((Yn(w) - $) / 1e3)), kn.textAlign = "left", Pr = kn.measureText(te), kn.fillText(te, v + (or - Pr.width) / 2, x)));
					return null
				}
			},
			xi = rn(150,
			function() {
				return oe(drawChart)
			}),
			loadingDiv = $("#loading"),
			vn = 1,
			Re = {},
			function() {
				var t;
				return t = 0,
				function() {
					var n;
					ue(3e3,
					function() {
						function e() {
							n = Date.now() - t,
							n > 3e4 ? $("#pc_to_bw").attr("class", "bad") : n > 15e3 ? $("#pc_to_bw").attr("class", "normal") : $("#pc_to_bw").attr("class", "good"),
							ue(1e3,
							function(t) {
								e(t)
							})
						}
						e()
					})
				} (),
				null
			} (),
			windowObj.ready(function() {
				resizeBlocks(),
				setTimeout(function() {
					$(".nice-scroll").getNiceScroll().resize()
				},
				300)
			}),
			windowObj.resize(function() {
				resizeBlocks(),
				setTimeout(function() {
					$(".nice-scroll").getNiceScroll().resize()
				},
				300);
				return a && Mi(),
				xi()
			}),
			Wr.hover(function() {
				return Wr.height(320)
			},
			function() {
				return Wr.height(32)
			}),
			oi.mousewheel(function(t, n) {
				return n > 0 ? ur += 2 : ur -= 2,
				3 > ur && (ur = 3),
				ur > 27 && (ur = 27),
				Cr = Math.round(.2 * ur),
				3 > Cr && (Cr = 3),
				3 === ur && (Cr = 2),
				sr = ur + Cr,
				$i = (ur - 1) / 2,
				storeChartInfoIntoCookie("barWidth", ur),
				storeChartInfoIntoCookie("gapWidth", Cr),
				drawChart(),
				!1
			}),
			Kn = $("#settings"),
			$("#btn_settings").click(function() {
				return $("#settings").is(":visible") ? Kn.hide() : Kn.show(),
				!0
			}),
			$("#close_settings").click(function() {
				return $("#settings").is(":visible") ? Kn.hide() : Kn.show(),
				!0
			}),
			io = function(t, n) {
				function e() {
					var n, e, r, o, u, l;
					for (r = on[t].params, l = [], n = o = 0, u = i.length; u > o; n = ++o) e = i[n],
					l.push($(e).val(r[n]));
					return l
				}
				var r, i, o, u;
				if (n.default_params = n.params, r = n.cookie, i = $("input[name=" + t + "]"), i.change(function() {
					var n, e, o, u;
					for (e = [], o = 0, u = i.length; u > o; o++) {
						if (n = i[o], yi = $(n).val(), !yi.match(/^\d+$/)) {
							if ("price_mas" === t && "" === yi) continue;
							return alert(yi + " is not integer."),
							void 0
						}
						e.push(parseInt(yi))
					}
					return $.cookie(r, JSON.stringify(e), {
						expires: 3650,
						path: "/"
					}),
					on[t].params = e,
					be = {},
					be[ye] = ce,
					Yi()
				}), $("#indicator_" + t + " button").click(function() {
					return on[t].params = on[t].default_params,
					e(),
					$(i[0]).change()
				}), u = $.cookie(r)) try {
					o = JSON.parse(u),
					on[t].params = o
				} catch(l) {}
				return e()
			};
			for (Rr in on) me.call(on, Rr) && (Sr = on[Rr], io(Rr, Sr));
			nn = !0,
			x = {
				USD: "$",
				EUR: "€",
				GBP: "£",
				CNY: "¥",
				JPY: "¥",
				AUD: "A$",
				CAD: "C$",
				BTC: "฿",
				LTC: "Ł"
			},
			Ae = {},
			_e = {},
			K = {},
			Rn = null,
			xe = {},
			te = {},
			function() {
				function t() {
					Er && (drawTickInfo(), drawMainChart(), Er = false),
					ue(80,
					function(n) {
						t(n)
					})
				}
				t()
			} (),
			function() {
				function t() {
					Nr && (Nr = false),
					ue(120,
					function(n) {
						t(n)
					})
				}
				t()
			} (),
			function() {
				return $("#main").show(),
				$("#footer").show()
			} (),
			drawChart(),
			eo()
		})
	} ()
}.call(this);


function toggleMingxi(){
	
	window.open("/u/transaction/entrustdeatils");
	
}

$(function() {
	getCurr();
	timeChatBaddy();
	
});

