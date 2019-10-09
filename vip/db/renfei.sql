ALTER TABLE `btcdetails`
ADD UNIQUE INDEX `uniq_txid` (`addHash`),
DROP INDEX `addHash` ;

ALTER TABLE `etcdetails`
ADD UNIQUE INDEX `uniq_txid` (`addHash`),
DROP INDEX `addHash` ;

ALTER TABLE `ltcdetails`
ADD UNIQUE INDEX `uniq_txid` (`addHash`),
DROP INDEX `addHash` ;

ALTER TABLE `mgdetails`
ADD UNIQUE INDEX `uniq_txid` (`addHash`),
DROP INDEX `addHash` ;



ALTER TABLE `btckey`
ADD UNIQUE INDEX `uniq_keypre` (`keyPre`);
ALTER TABLE `etckey`
ADD UNIQUE INDEX `uniq_keypre` (`keyPre`);
ALTER TABLE `ltckey`
ADD UNIQUE INDEX `uniq_keypre` (`keyPre`);
ALTER TABLE `mgkey`
ADD UNIQUE INDEX `uniq_keypre` (`keyPre`);