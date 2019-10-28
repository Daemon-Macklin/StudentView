-- phpMyAdmin SQL Dump
-- version 4.8.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: Oct 28, 2019 at 01:19 AM
-- Server version: 5.7.26
-- PHP Version: 7.2.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `studentdatabase`
--

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
CREATE TABLE IF NOT EXISTS `students` (
  `SID` int(5) NOT NULL,
  `STUD_ID` int(8) NOT NULL,
  `FNAME` varchar(20) COLLATE sjis_bin NOT NULL,
  `SNAME` varchar(20) COLLATE sjis_bin NOT NULL,
  PRIMARY KEY (`SID`)
) ENGINE=InnoDB DEFAULT CHARSET=sjis COLLATE=sjis_bin;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`SID`, `STUD_ID`, `FNAME`, `SNAME`) VALUES
(12345, 12345678, 'Daemon', 'Macklin'),
(43563, 12342257, 'Jerry', 'Hood'),
(44444, 44444444, 'Chris', 'Phillips'),
(54321, 24689753, 'John', 'Goodman'),
(55555, 55555555, 'Justin', 'Falk'),
(99999, 9999999, 'Bob', 'Builder');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `UID` int(5) NOT NULL,
  `UNAME` varchar(20) COLLATE sjis_bin NOT NULL,
  PRIMARY KEY (`UID`)
) ENGINE=InnoDB DEFAULT CHARSET=sjis COLLATE=sjis_bin;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UID`, `UNAME`) VALUES
(1234, 'Daemon'),
(55555, 'Jon');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
