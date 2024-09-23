using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Web;

namespace Tester
{
    internal class Message(string text, uint senderId, uint receiverId)
    {
        public uint SenderId { get; set; } = senderId;
        public uint ReceiverId { get; set; } = receiverId;
        public DateTime SentTime { get; } = DateTime.Now;
        public string Text { get; set; } = text;
    }
}
