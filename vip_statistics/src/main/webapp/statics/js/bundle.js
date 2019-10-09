! function (e, t) {
    "object" == typeof exports && "undefined" != typeof module ? t(exports) : "function" == typeof define && define.amd ? define(["exports"], t) : t(e.Datafeeds = {})
}(this, function (e) {
    "use strict";

    function t(e) {
        if (a) {
            var t = new Date;
            console.log(t.toLocaleTimeString() + "." + t.getMilliseconds() + "> " + e)
        }
    }

    function r(e) {
        return void 0 === e ? "" : "string" == typeof e ? e : e.message
    }

    function s(e, t, r) {
        var s = e[t];
        return Array.isArray(s) ? s[r] : s
    }

    function o(e, t) {
        return void 0 !== e ? e : t
    }

    function i(e, t, r) {
        var s = e[t];
        return Array.isArray(s) ? s[r] : s
    }
    var n = Object.setPrototypeOf || {
        __proto__: []
    }
    instanceof Array && function (e, t) {
        e.__proto__ = t
    } || function (e, t) {
        for (var r in t) t.hasOwnProperty(r) && (e[r] = t[r])
    }, a = !1, u = function () {
        function e(e, t) {
            this._datafeedUrl = e, this._requester = t
        }
        return e.prototype.getBars = function (e, t, s, o) {
            var i = this,
                n = {
                    symbol: void 0 !== e.ticker ? e.ticker.toUpperCase() : "",
                    resolution: t,
                    from: s,
                    to: o
                };
            return new Promise(function (e, t) {
                i._requester.sendRequest(i._datafeedUrl, "klineLastData", n).then(function (r) {
                    if ("ok" === r.s || "no_data" === r.s) {
                        var s = [],
                            o = {
                                noData: !1
                            };
                        if ("no_data" === r.s) o.noData = !0, o.nextTime = r.nextTime;
                        else
                            for (var i = void 0 !== r.v, n = void 0 !== r.o, a = 0; a < r.t.length; ++a) {
                                var u = {
                                    time: 1e3 * r.t[a],
                                    close: Number(r.c[a]),
                                    open: Number(r.c[a]),
                                    high: Number(r.c[a]),
                                    low: Number(r.c[a])
                                };
                                n && (u.open = Number(r.o[a]), u.high = Number(r.h[a]), u.low = Number(r.l[a])), i && (u.volume = Number(r.v[a])), s.push(u)
                            }
                        e({
                            bars: s,
                            meta: o
                        })
                    } else t(r.errmsg)
                }).catch(function (e) {
                    var s = r(e);
                    console.warn("HistoryProvider: getBars() failed, error=" + s), t(s)
                })
            })
        }, e
    }(), c = function () {
        function e(e, t) {
            this._subscribers = {}, this._requestsPending = 0, this._historyProvider = e, setInterval(this._updateData.bind(this), t)
        }
        return e.prototype.subscribeBars = function (e, r, s, o) {
            this._subscribers.hasOwnProperty(o) ? t("DataPulseProvider: already has subscriber with id=" + o) : (this._subscribers[o] = {
                lastBarTime: null,
                listener: s,
                resolution: r,
                symbolInfo: e
            }, t("DataPulseProvider: subscribed for #" + o + " - {" + e.name + ", " + r + "}"))
        }, e.prototype.unsubscribeBars = function (e) {
            delete this._subscribers[e], t("DataPulseProvider: unsubscribed for #" + e)
        }, e.prototype._updateData = function () {
            var e = this;
            if (!(this._requestsPending > 0)) {
                this._requestsPending = 0;
                var s = function (s) {
                        o._requestsPending += 1, o._updateDataForSubscriber(s).then(function () {
                            e._requestsPending -= 1, t("DataPulseProvider: data for #" + s + " updated successfully, pending=" + e._requestsPending)
                        }).catch(function (o) {
                            e._requestsPending -= 1, t("DataPulseProvider: data for #" + s + " updated with error=" + r(o) + ", pending=" + e._requestsPending)
                        })
                    },
                    o = this;
                for (var i in this._subscribers) s(i)
            }
        }, e.prototype._updateDataForSubscriber = function (e) {
            var t = this,
                r = this._subscribers[e],
                s = parseInt((Date.now() / 1e3).toString()),
                o = s - function (e, t) {
                    return 24 * ("D" === e ? t : "M" === e ? 31 * t : "W" === e ? 7 * t : t * parseInt(e) / 1440) * 60 * 60
                }(r.resolution, 10);
            return this._historyProvider.getBars(r.symbolInfo, r.resolution, o, s).then(function (r) {
                t._onSubscriberDataReceived(e, r)
            })
        }, e.prototype._onSubscriberDataReceived = function (e, r) {
            if (this._subscribers.hasOwnProperty(e)) {
                var s = r.bars;
                if (0 !== s.length) {
                    var o = s[s.length - 1],
                        i = this._subscribers[e];
                    if (!(null !== i.lastBarTime && o.time < i.lastBarTime)) {
                        if (null !== i.lastBarTime && o.time > i.lastBarTime) {
                            if (s.length < 2) throw new Error("Not enough bars in history for proper pulse update. Need at least 2.");
                            var n = s[s.length - 2];
                            i.listener(n)
                        }
                        i.lastBarTime = o.time, i.listener(o)
                    }
                }
            } else t("DataPulseProvider: Data comes for already unsubscribed subscription #" + e)
        }, e
    }(), l = function () {
        function e(e) {
            this._subscribers = {}, this._requestsPending = 0, this._quotesProvider = e, setInterval(this._updateQuotes.bind(this, 1), 1e4), setInterval(this._updateQuotes.bind(this, 0), 6e4)
        }
        return e.prototype.subscribeQuotes = function (e, r, s, o) {
            this._subscribers[o] = {
                symbols: e,
                fastSymbols: r,
                listener: s
            }, t("QuotesPulseProvider: subscribed quotes with #" + o)
        }, e.prototype.unsubscribeQuotes = function (e) {
            delete this._subscribers[e], t("QuotesPulseProvider: unsubscribed quotes with #" + e)
        }, e.prototype._updateQuotes = function (e) {
            var s = this;
            if (!(this._requestsPending > 0)) {
                var o = function (o) {
                        i._requestsPending++;
                        var n = i._subscribers[o];
                        i._quotesProvider.getQuotes(1 === e ? n.fastSymbols : n.symbols).then(function (r) {
                            s._requestsPending--, s._subscribers.hasOwnProperty(o) && (n.listener(r), t("QuotesPulseProvider: data for #" + o + " (" + e + ") updated successfully, pending=" + s._requestsPending))
                        }).catch(function (i) {
                            s._requestsPending--, t("QuotesPulseProvider: data for #" + o + " (" + e + ") updated with error=" + r(i) + ", pending=" + s._requestsPending)
                        })
                    },
                    i = this;
                for (var n in this._subscribers) o(n)
            }
        }, e
    }(), h = function () {
        function e(e, t, r) {
            this._exchangesList = ["NYSE", "FOREX", "AMEX"], this._symbolsInfo = {}, this._symbolsList = [], this._datafeedUrl = e, this._datafeedSupportedResolutions = t, this._requester = r, this._readyPromise = this._init(), this._readyPromise.catch(function (e) {
                console.error("SymbolsStorage: Cannot init, error=" + e.toString())
            })
        }
        return e.prototype.resolveSymbol = function (e) {
            var t = this;
            return this._readyPromise.then(function () {
                var r = t._symbolsInfo[e];
                return void 0 === r ? Promise.reject("invalid symbol") : Promise.resolve(r)
            })
        }, e.prototype.searchSymbols = function (e, t, r, s) {
            var o = this;
            return this._readyPromise.then(function () {
                var i = [],
                    n = 0 === e.length;
                e = e.toUpperCase();
                for (var a = function (s) {
                        var a = o._symbolsInfo[s];
                        if (void 0 === a) return "continue";
                        if (r.length > 0 && a.type !== r) return "continue";
                        if (t && t.length > 0 && a.exchange !== t) return "continue";
                        var u = a.name.toUpperCase().indexOf(e),
                            c = a.description.toUpperCase().indexOf(e);
                        if (n || u >= 0 || c >= 0) {
                            if (!i.some(function (e) {
                                    return e.symbolInfo === a
                                })) {
                                var l = u >= 0 ? u : 8e3 + c;
                                i.push({
                                    symbolInfo: a,
                                    weight: l
                                })
                            }
                        }
                    }, u = 0, c = o._symbolsList; u < c.length; u++) {
                    a(c[u])
                }
                var l = i.sort(function (e, t) {
                    return e.weight - t.weight
                }).slice(0, s).map(function (e) {
                    var t = e.symbolInfo;
                    return {
                        symbol: t.name,
                        full_name: t.full_name,
                        description: t.description,
                        exchange: t.exchange,
                        params: [],
                        type: t.type,
                        ticker: t.name
                    }
                });
                return Promise.resolve(l)
            })
        }, e.prototype._init = function () {
            for (var e = this, r = [], s = {}, o = 0, i = this._exchangesList; o < i.length; o++) {
                var n = i[o];
                s[n] || (s[n] = !0, r.push(this._requestExchangeData(n)))
            }
            return Promise.all(r).then(function () {
                e._symbolsList.sort(), t("SymbolsStorage: All exchanges data loaded")
            })
        }, e.prototype._requestExchangeData = function (e) {
            var s = this;
            return new Promise(function (o, i) {
                s._requester.sendRequest(s._datafeedUrl, "symbol_info", {
                    group: e
                }).then(function (t) {
                    try {
                        s._onExchangeDataReceived(e, t)
                    } catch (e) {
                        return void i(e)
                    }
                    o()
                }).catch(function (s) {
                    t("SymbolsStorage: Request data for exchange '" + e + "' failed, reason=" + r(s)), o()
                })
            })
        }, e.prototype._onExchangeDataReceived = function (e, t) {
            var r = 0;
            try {
                for (var i = t.symbol.length, n = void 0 !== t.ticker; r < i; ++r) {
                    var a = t.symbol[r],
                        u = s(t, "exchange-listed", r),
                        c = s(t, "exchange-traded", r),
                        l = c + ":" + a,
                        h = n ? s(t, "ticker", r) : a,
                        f = {
                            ticker: h,
                            name: a,
                            base_name: [u + ":" + a],
                            full_name: l,
                            listed_exchange: u,
                            exchange: c,
                            description: s(t, "description", r),
                            has_intraday: o(s(t, "has-intraday", r), !1),
                            has_no_volume: o(s(t, "has-no-volume", r), !1),
                            minmov: s(t, "minmovement", r) || s(t, "minmov", r) || 0,
                            minmove2: s(t, "minmove2", r) || s(t, "minmov2", r),
                            fractional: s(t, "fractional", r),
                            pricescale: s(t, "pricescale", r),
                            type: s(t, "type", r),
                            session: s(t, "session-regular", r),
                            timezone: s(t, "timezone", r),
                            supported_resolutions: o(s(t, "supported-resolutions", r), this._datafeedSupportedResolutions),
                            force_session_rebuild: s(t, "force-session-rebuild", r),
                            has_daily: o(s(t, "has-daily", r), !0),
                            intraday_multipliers: o(s(t, "intraday-multipliers", r), ["1", "5", "15", "30", "60"]),
                            has_weekly_and_monthly: s(t, "has-weekly-and-monthly", r),
                            has_empty_bars: s(t, "has-empty-bars", r),
                            volume_precision: o(s(t, "volume-precision", r), 0)
                        };
                    this._symbolsInfo[h] = f, this._symbolsInfo[a] = f, this._symbolsInfo[l] = f, this._symbolsList.push(a)
                }
            } catch (s) {
                throw new Error("SymbolsStorage: API error when processing exchange " + e + " symbol #" + r + " (" + t.symbol[r] + "): " + s.message)
            }
        }, e
    }(), f = function () {
        function e(e, t, r, s) {
            void 0 === s && (s = 1e4);
            var o = this;
            this._configuration = {
                supports_search: !1,
                supports_group_request: !0,
                supported_resolutions: ["1", "5", "15", "30", "60", "1D", "1W", "1M"],
                supports_marks: !1,
                supports_timescale_marks: !1
            }, this._symbolsStorage = null, this._datafeedURL = e, this._requester = r, this._historyProvider = new u(e, this._requester), this._quotesProvider = t, this._dataPulseProvider = new c(this._historyProvider, s), this._quotesPulseProvider = new l(this._quotesProvider), this._configurationReadyPromise = this._requestConfiguration().then(function (e) {
                null === e && (e = {
                    supports_search: !1,
                    supports_group_request: !0,
                    supported_resolutions: ["1", "5", "15", "30", "60", "1D", "1W", "1M"],
                    supports_marks: !1,
                    supports_timescale_marks: !1
                }), o._setupWithConfiguration(e)
            })
        }
        return e.prototype.onReady = function (e) {
            var t = this;
            this._configurationReadyPromise.then(function () {
                e(t._configuration)
            })
        }, e.prototype.getQuotes = function (e, t, r) {
            this._quotesProvider.getQuotes(e).then(t).catch(r)
        }, e.prototype.subscribeQuotes = function (e, t, r, s) {
            this._quotesPulseProvider.subscribeQuotes(e, t, r, s)
        }, e.prototype.unsubscribeQuotes = function (e) {
            this._quotesPulseProvider.unsubscribeQuotes(e)
        }, e.prototype.calculateHistoryDepth = function (e, t, r) {}, e.prototype.getMarks = function (e, s, o, n, a) {
            if (this._configuration.supports_marks) {
                var u = {
                    symbol: void 0 !== e.ticker ? e.ticker.toUpperCase() : "",
                    from: s,
                    to: o,
                    resolution: a
                };
                this._send("marks", u).then(function (e) {
                    if (!Array.isArray(e)) {
                        for (var t = [], r = 0; r < e.id.length; ++r) t.push({
                            id: i(e, "id", r),
                            time: i(e, "time", r),
                            color: i(e, "color", r),
                            text: i(e, "text", r),
                            label: i(e, "label", r),
                            labelFontColor: i(e, "labelFontColor", r),
                            minSize: i(e, "minSize", r)
                        });
                        e = t
                    }
                    n(e)
                }).catch(function (e) {
                    t("UdfCompatibleDatafeed: Request marks failed: " + r(e)), n([])
                })
            }
        }, e.prototype.getTimescaleMarks = function (e, s, o, n, a) {
            if (this._configuration.supports_timescale_marks) {
                var u = {
                    symbol: void 0 !== e.ticker ? e.ticker.toUpperCase() : "",
                    from: s,
                    to: o,
                    resolution: a
                };
                this._send("timescale_marks", u).then(function (e) {
                    if (!Array.isArray(e)) {
                        for (var t = [], r = 0; r < e.id.length; ++r) t.push({
                            id: i(e, "id", r),
                            time: i(e, "time", r),
                            color: i(e, "color", r),
                            label: i(e, "label", r),
                            tooltip: i(e, "tooltip", r)
                        });
                        e = t
                    }
                    n(e)
                }).catch(function (e) {
                    t("UdfCompatibleDatafeed: Request timescale marks failed: " + r(e)), n([])
                })
            }
        }, e.prototype.getServerTime = function (e) {
            this._configuration.supports_time && this._send("time").then(function (t) {
                var r = parseInt(t);
                isNaN(r) || e(r)
            }).catch(function (e) {
                t("UdfCompatibleDatafeed: Fail to load server time, error=" + r(e))
            })
        }, e.prototype.searchSymbols = function (e, s, o, i) {
            if (this._configuration.supports_search) {
                var n = {
                    limit: 30,
                    query: e.toUpperCase(),
                    type: o,
                    exchange: s
                };
                this._send("search", n).then(function (e) {
                    if (void 0 !== e.s) return t("UdfCompatibleDatafeed: search symbols error=" + e.errmsg), void i([]);
                    i(e)
                }).catch(function (s) {
                    t("UdfCompatibleDatafeed: Search symbols for '" + e + "' failed. Error=" + r(s)), i([])
                })
            } else {
                if (null === this._symbolsStorage) throw new Error("UdfCompatibleDatafeed: inconsistent configuration (symbols storage)");
                this._symbolsStorage.searchSymbols(e, s, o, 30).then(i).catch(i.bind(null, []))
            }
        }, e.prototype.resolveSymbol = function (e, s, o) {
            function i(e) {
                t("Symbol resolved: " + (Date.now() - n) + "ms"), s(e)
            }
            t("Resolve requested");
            var n = Date.now();
            if (this._configuration.supports_group_request) {
                if (null === this._symbolsStorage) throw new Error("UdfCompatibleDatafeed: inconsistent configuration (symbols storage)");
                this._symbolsStorage.resolveSymbol(e).then(i).catch(o)
            } else {
                var a = {
                    symbol: e.toUpperCase()
                };
                $.getJSON(DOMAIN_TRANS + "/getMarket?callback=?&type=" + a, function(result) {
                    if (result.isSuc && result.datas.length > 0) {
                        var json = result.datas[0];
                        var exchangeBixDian = parseInt(json.exchangeBixDian);
                        var pricescale = Math.pow(10, exchangeBixDian);
                        var b = {
                            "name": a,
                            "full_name": a,
                            "symbol": a,
                            "exchange": "BitGlobal",
                            "exchange-traded": "BitGlobal",
                            "exchange-listed": "BitGlobal",
                            "timezone": "UTC",
                            "pricescale": pricescale,
                            "minmov": 1,
                            "minmove2": 0,
                            "has_intraday": true,
                            "intraday_multipliers": ["1", "5", "60", "1440"],
                            "has_daily": true,
                            "has_weekly_and_monthly": false,
                            "has_empty_bars": false,
                            "force_session_rebuild": false,
                            "has_no_volume": false,
                            "has_fractional_volume": false,
                            "ticker": a,
                            "description": "",
                            "session": "24x7",
                            "data_status": "streaming",
                            "supported_resolutions": ["1", "3", "5", "15", "30", "60", "120", "180", "240", "360", "720", "D", "3D", "1W", "2W", "1M"],
                            "type": "bitcoin"
                        };
                        setTimeout(function(){
                            SYMBOL_STORAGE[b.symbol] = b, b.s && "ok" != b.s ? o("unknown_symbol") : i(b)
                        }, 0);
                    }
                });
                // this._send("symbols", a).then(function (e) {
                //     void 0 !== e.s ? o("unknown_symbol") : i(e)
                // }).catch(function (e) {
                //     t("UdfCompatibleDatafeed: Error resolving symbol: " + r(e)), o("unknown_symbol")
                // })
            }
        }, e.prototype.getBars = function (e, t, r, s, o, i) {
            this._historyProvider.getBars(e, t, r, s).then(function (e) {
                o(e.bars, e.meta)
            }).catch(i)
        }, e.prototype.subscribeBars = function (e, t, r, s, o) {
            this._dataPulseProvider.subscribeBars(e, t, r, s)
        }, e.prototype.unsubscribeBars = function (e) {
            this._dataPulseProvider.unsubscribeBars(e)
        }, e.prototype._requestConfiguration = function () {
            return this._send("config").catch(function (e) {
                return t("UdfCompatibleDatafeed: Cannot get datafeed configuration - use default, error=" + r(e)), null
            })
        }, e.prototype._send = function (e, t) {
            return this._requester.sendRequest(this._datafeedURL, e, t)
        }, e.prototype._setupWithConfiguration = function (e) {
            if (this._configuration = e, void 0 === e.exchanges && (e.exchanges = []), !e.supports_search && !e.supports_group_request) throw new Error("Unsupported datafeed configuration. Must either support search, or support group request");
            !e.supports_group_request && e.supports_search || (this._symbolsStorage = new h(this._datafeedURL, e.supported_resolutions || [], this._requester)), t("UdfCompatibleDatafeed: Initialized with " + JSON.stringify(e))
        }, e
    }(), d = function () {
        function e(e, t) {
            this._datafeedUrl = e, this._requester = t
        }
        return e.prototype.getQuotes = function (e) {
            var s = this;
            return new Promise(function (o, i) {
                s._requester.sendRequest(s._datafeedUrl, "quotes", {
                    symbols: e
                }).then(function (e) {
                    "ok" === e.s ? o(e.d) : i(e.errmsg)
                }).catch(function (e) {
                    var s = r(e);
                    t("QuotesProvider: getQuotes failed, error=" + s), i("network error: " + s)
                })
            })
        }, e
    }(), p = function () {
        function e(e) {
            e && (this._headers = e)
        }
        return e.prototype.sendRequest = function (e, r, s) {
            if (void 0 !== s) {
                var o = Object.keys(s);
                0 !== o.length && (r += "?"), r += o.map(function (e) {
                    return encodeURIComponent(e) + "=" + encodeURIComponent(s[e].toString())
                }).join("&")
            }
            t("New request: " + r);
            var i = {};
            return void 0 !== this._headers && (i.headers = this._headers), fetch(e + "/" + r, i).then(function (e) {
                return e.text()
            }).then(function (e) {
                return JSON.parse(e)
            })
        }, e
    }(), _ = function (e) {
        function t(t, r) {
            void 0 === r && (r = 1e4);
            var s = new p,
                o = new d(t, s);
            return e.call(this, t, o, s, r) || this
        }
        return function (e, t) {
            function r() {
                this.constructor = e
            }
            n(e, t), e.prototype = null === t ? Object.create(t) : (r.prototype = t.prototype, new r)
        }(t, e), t
    }(f);
    e.UDFCompatibleDatafeed = _, Object.defineProperty(e, "__esModule", {
        value: !0
    })
});