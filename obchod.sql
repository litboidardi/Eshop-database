-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: Po 09.Jan 2023, 23:01
-- Verzia serveru: 10.4.24-MariaDB
-- Verzia PHP: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `obchod`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `kosik`
--

CREATE TABLE `kosik` (
  `ID` int(11) NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `kosik`
--

INSERT INTO `kosik` (`ID`, `ID_pouzivatela`, `ID_tovaru`, `cena`, `ks`) VALUES
(117, 1, 3, 120, 2),
(83, 9, 5, 3, 1),
(88, 9, 2, 2, 1),
(87, 9, 3, 8, 2);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_polozky`
--

CREATE TABLE `obj_polozky` (
  `ID` int(11) NOT NULL,
  `ID_objednavky` int(11) NOT NULL,
  `ID_tovaru` int(11) NOT NULL,
  `cena` int(11) NOT NULL,
  `ks` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `obj_polozky`
--

INSERT INTO `obj_polozky` (`ID`, `ID_objednavky`, `ID_tovaru`, `cena`, `ks`) VALUES
(70, 60, 3, 120, 2),
(69, 59, 7, 60, 1),
(68, 58, 3, 60, 1),
(67, 57, 1, 240, 2),
(66, 57, 6, 44, 2);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `obj_zoznam`
--

CREATE TABLE `obj_zoznam` (
  `ID` int(11) NOT NULL,
  `obj_cislo` varchar(20) NOT NULL,
  `datum_objednavky` date NOT NULL,
  `ID_pouzivatela` int(11) NOT NULL,
  `suma` int(11) NOT NULL,
  `stav` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `obj_zoznam`
--

INSERT INTO `obj_zoznam` (`ID`, `obj_cislo`, `datum_objednavky`, `ID_pouzivatela`, `suma`, `stav`) VALUES
(58, '20230109201730', '2023-01-09', 2, 58, 'Processed'),
(59, '20230109202721', '2023-01-09', 17, 60, 'being processed'),
(60, '20230109212634', '2023-01-09', 1, 96, 'being processed'),
(57, '20230109201643', '2023-01-09', 1, 227, 'Sent');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `sklad`
--

CREATE TABLE `sklad` (
  `ID` int(11) NOT NULL,
  `nazov` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `obrazok` varchar(1000) NOT NULL,
  `ks` int(11) NOT NULL,
  `cena` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `sklad`
--

INSERT INTO `sklad` (`ID`, `nazov`, `obrazok`, `ks`, `cena`) VALUES
(1, 'Monke', 'https://pngimg.com/uploads/monkey/monkey_PNG18743.png', 2, 120),
(2, 'Pingu', 'https://assets.stickpng.com/thumbs/589c7eea64b351149f22a81a.png', 1, 200),
(3, 'Denis', 'https://pngimg.com/d/goat_PNG13155.png', 11, 60),
(5, 'Magicarp', 'https://www.serebii.net/scarletviolet/pokemon/new/129.png', 93, 3),
(6, 'Kevin', 'https://freepngdesign.com/content/uploads/images/pigeon-1495.png', 28, 22),
(7, 'Steve Harvey', 'https://www.pngarts.com/files/3/Mr-Potato-Head-Transparent-Images.png', 10, 60);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `users`
--

CREATE TABLE `users` (
  `ID` int(11) NOT NULL,
  `login` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `passwd` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `adresa` varchar(50) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `zlava` int(11) NOT NULL,
  `meno` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `priezvisko` varchar(20) CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `poznamky` text CHARACTER SET utf8 COLLATE utf8_slovak_ci NOT NULL,
  `je_admin` tinyint(1) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Sťahujem dáta pre tabuľku `users`
--

INSERT INTO `users` (`ID`, `login`, `passwd`, `adresa`, `zlava`, `meno`, `priezvisko`, `poznamky`, `je_admin`) VALUES
(1, 'jskalka@ukf.sk', '123', 'Zeleninova 4, Nitra', 20, 'Jan ', 'Skalka', 'tester', 0),
(2, 'jmrkva@ukf.sk', '123', 'Zahrada 11', 3, 'Jozef', 'Mrkva', 'druhý tester', 0),
(19, 'jmai@ukf.sk', '123', 'Long Island, USA', 0, 'Jari', 'Mai', 'new user', 0),
(4, 'gfieri@ukf.sk', '123', '76 Abbey Rd, London', 0, 'Guy', 'Fieri', 'new user', 0),
(18, 'darci@ukf.com', '123', 'Družstevná 17', 0, 'Dárius', 'Pintér', 'new user', 0),
(3, 'admin@ukf.sk', '123', 'Adminov dom, Adminovo', 0, 'Admin', 'Adminovský', 'admin', 1),
(5, 'sgreen@ukf.sk', '123', 'Tr. A. Hlinku 1, Nitra', 0, 'Stephen', 'Green', 'new user', 0);

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  ADD PRIMARY KEY (`ID`);

--
-- Indexy pre tabuľku `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `kosik`
--
ALTER TABLE `kosik`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=119;

--
-- AUTO_INCREMENT pre tabuľku `obj_polozky`
--
ALTER TABLE `obj_polozky`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT pre tabuľku `obj_zoznam`
--
ALTER TABLE `obj_zoznam`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=61;

--
-- AUTO_INCREMENT pre tabuľku `sklad`
--
ALTER TABLE `sklad`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pre tabuľku `users`
--
ALTER TABLE `users`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
